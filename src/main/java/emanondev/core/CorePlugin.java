package emanondev.core;

import emanondev.core.command.CoreCommand;
import emanondev.core.command.ReloadCommand;
import emanondev.core.gui.Gui;
import emanondev.core.packetentity.PacketManager;
import emanondev.core.spigot.Metrics;
import emanondev.core.spigot.UpdateChecker;
import emanondev.core.sql.SQLDatabase;
import emanondev.core.sql.SQLType;
import emanondev.core.utility.ConsoleHelper;
import emanondev.core.utility.ReflectionUtility;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public abstract class CorePlugin extends JavaPlugin implements ConsoleHelper {

    private final HashMap<String, YMLConfig> languageConfigs = new HashMap<>();
    private final HashMap<String, YMLConfig> configs = new HashMap<>();
    private final HashSet<Command> registeredCommands = new HashSet<>();
    private final EnumMap<CounterAPI.ResetTime, CounterAPI> counterApiMap = new EnumMap<>(CounterAPI.ResetTime.class);
    private final EnumMap<CounterAPI.ResetTime, CounterAPI> persistentCounterApiMap = new EnumMap<>(CounterAPI.ResetTime.class);
    private final Set<String> registeredPermissions = new HashSet<>();
    private final HashSet<SQLDatabase> connections = new HashSet<>();
    private final Map<String, Module> modules = new HashMap<>();
    private final Set<Module> enabledModules = new HashSet<>();
    private LoggerManager loggerManager;
    private boolean useMultiLanguage = true;
    @Getter
    private String defaultLocale;
    private CooldownAPI cooldownApi = null;
    private CooldownAPI persistentCooldownApi = null;
    private PacketManager packetManager = null;
    private BukkitAudiences adventure;

    @SuppressWarnings("unchecked")
    private static Map<String, Command> getKnownCommands(Object object) throws Exception {
        Field objectField = ReflectionUtility.getDeclaredField(SimpleCommandMap.class,"knownCommands");
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        ReflectionUtility.getFieldValue(object,"knownCommands");
        return (Map<String, Command>) result;
    }

    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    /**
     * Enable the plugin. <br>
     * <br>
     * Prepare for handling commands.<br>
     * Register Command Reload for this if {@link #registerReloadCommand()}.<br>
     * Calls {@link #enable()}.
     */
    public final void onEnable() {
        long now = System.currentTimeMillis();

        this.adventure = BukkitAudiences.create(this);
        setupLanguageConfig();

        if (registerReloadCommand()) registerCommand(new ReloadCommand(this));
        enable();
        logPentaStar(ChatColor.YELLOW, "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
    }

    public boolean useMultiLanguage() {
        return useMultiLanguage;
    }

    /**
     * @return Unmodifiable set of commands registered by this
     */
    @NotNull
    public Set<Command> getRegisteredCommands() {
        return Collections.unmodifiableSet(registeredCommands);
    }

    /**
     * Reload the Plugin.<br>
     * <br>
     * Reloads file configurations.<br>
     * Reloads loggers configurations.<br>
     * Calls {@link #reload()}.<br>
     * TODO reloads SQL config.<br>
     */
    public final void onReload() {
        long now = System.currentTimeMillis();

        for (YMLConfig conf : configs.values()) {
            try {
                if (conf.getFile().exists()) conf.reload();
            } catch (Exception e) {
                log.error("Unable to reload file {}", conf.getFileName(), e);
            }
        }

        setupLanguageConfig();
        languageConfigs.clear();
        getLanguageConfig(null);
        if (loggerManager != null) loggerManager.reload();

        try {
            reload();
        } catch (Exception e) {
            log.error("Unable execute reload()", e);
        }
        for (Command command : registeredCommands) {
            if (command instanceof CoreCommand coreCommand)
                coreCommand.reload();
        }

        for (Module module : modules.values()) {
            if (getConfig("modules.yml").loadBoolean(module.getID() + ".enable", true) != enabledModules.contains(module)) {
                //enabledModules.put(module.getID(), !enabledModules.get(module.getID()));
                if (!enabledModules.contains(module)) {
                    enabledModules.add(module);
                    module.enable();
                    this.registerListener(module);
                    this.logDone("Enabled module &e" + module.getID());
                } else {
                    enabledModules.remove(module);
                    HandlerList.unregisterAll(module);
                    module.disable();
                    this.logDone("Disabled module &e" + module.getID());
                }
            }
            if (enabledModules.contains(module)) {
                module.reload();
            }
        }
        logPentaStar(ChatColor.YELLOW, "Reloaded (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
    }

    /**
     * Gets a Config file based on sender language, or default language if sender is
     * not a player
     *
     * @param sender The target of language
     * @return Config file for sender language
     */
    @NotNull
    public YMLConfig getLanguageConfig(final @Nullable CommandSender sender) {
        String locale;
        if (!(sender instanceof Player)) {
            locale = this.defaultLocale;
        } else if (this.useMultiLanguage) {
            locale = ((Player) sender).getLocale().split("_")[0];
        } else {
            locale = this.defaultLocale;
        }

        if (this.languageConfigs.containsKey(locale)) {
            return languageConfigs.get(locale);
        }
        String fileName = "language" + File.separator + locale + ".yml";
        if (locale.equals(this.defaultLocale) || new File(getDataFolder(), fileName).exists()
                || this.getResource("languages/" + locale + ".yml") != null) {
            YMLConfig conf = new YMLConfig(this, fileName);
            languageConfigs.put(locale, conf);
            return conf;
        }
        if (languageConfigs.containsKey(defaultLocale)) {
            return languageConfigs.get(defaultLocale);
        }
        fileName = "language" + File.separator + defaultLocale + ".yml";
        YMLConfig conf = new YMLConfig(this, fileName);
        languageConfigs.put(locale, conf);
        return conf;
    }

    private void setupLanguageConfig() {
        defaultLocale = getConfig().loadString("language.default-locale", "en");
        useMultiLanguage = getConfig().loadBoolean("language.multilanguage", true);

        if (useMultiLanguage) {
            this.logTetraStar(ChatColor.BLUE, "Default language &e" + defaultLocale);
        } else {
            this.logTetraStar(ChatColor.BLUE, "Language &e" + defaultLocale);
        }
    }

    /**
     * Reload the plugin. <br>
     * Called by {@link #onReload()}.
     */
    public abstract void reload();

    /**
     * Load the plugin. <br>
     * <br>
     * Reset the file "permissions.yml".<br>
     * Calls {@link #load()}.
     */
    public final void onLoad() {
        long now = System.currentTimeMillis();
        try {
            // TODO bad
            File file = new File(this.getDataFolder(), "permissions.yml");
            if (file.exists() && !file.delete()) {
                Exception e = new Exception();
                log.warn("Unable to delete file permissions.yml", e);
            }
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }
        load();
        logPentaStar(ChatColor.YELLOW, "Loaded (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
    }

    /**
     * Register the Listener (remember to put @EventHandler on listener
     * methods).<br>
     * Shortcut for {@link #getServer()}.{@link org.bukkit.Server#getPluginManager()
     * getPluginManager()}.{@link org.bukkit.plugin.PluginManager#registerEvents(Listener, org.bukkit.plugin.Plugin)
     * registerEvents(listener, this)};
     *
     * @param listener Listener to register
     * @throws NullPointerException if listener is null
     */
    public void registerListener(@NotNull Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * Unregister the Listener.<br>
     * Shortcut for {@link org.bukkit.event.HandlerList HandlerList}.{@link org.bukkit.event.HandlerList#unregisterAll() unregisterAll()};
     *
     * @param listener Listener to unregister
     * @throws NullPointerException if listener is null
     */
    public void unregisterListener(@NotNull Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    /**
     * Disable the Plugin.<br>
     * <br>
     * <p>
     * Calls {@link #disable()}.<br>
     * Disable all enabled modules.<br>
     * Unregister all registered commands.<br>
     * close SQL connections.
     */
    public final void onDisable() {
        long now = System.currentTimeMillis();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof Gui && this.equals(((Gui) p.getOpenInventory().getTopInventory().getHolder()).getPlugin())) {
                p.closeInventory();
                this.logDone("Safely closing gui for &e" + p.getName() + " &fto prevent dupe glitches");
            }
        }
        try {
            disable();
        } catch (Exception e) {
            log.error("Unexpected error invoking disable()", e);
        }
        if (persistentCooldownApi != null) {
            persistentCooldownApi.save();
            logDone("Saved &aCooldownAPI&f cache");
        }
        persistentCounterApiMap.forEach((k, v) -> {
            v.save();
            logDone("Saved &aCounterAPI " + k.name().toLowerCase(Locale.ENGLISH) + " &fcache");
        });

        modules.values().forEach(m -> {
            if (enabledModules.contains(m)) m.disable();
        });
        new HashSet<>(registeredCommands).forEach(this::unregisterCommand);

        if (this.packetManager != null) {
            packetManager.clearAll();
            logDone("Removed holograms and Packet entities");
        }
        for (SQLDatabase conn : new HashSet<>(connections)) {
            try {
                conn.disconnect(false);
                logDone(conn.getType().name() + " successfully disconnected from &e" + conn.getUrl());
            } catch (SQLException e) {
                log.error("Unexpected error", e);
            }
        }
        for (YMLConfig conf : configs.values()) {
            try {
                if (conf.getFile().exists()) if (conf.isDirty()) {
                    this.logDone("Force saving file &e" + conf.getFile().getPath() + "&f before turning off");
                    conf.save();
                }
            } catch (Exception e) {
                log.error("Unexpected error", e);
            }
        }
        for (String permission : registeredPermissions) {
            Bukkit.getPluginManager().removePermission(permission);
        }
        if (loggerManager != null) {
            loggerManager.disable();
        }
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        logPentaStar(ChatColor.YELLOW, "Disabled  (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
    }

    /**
     * Register permission to the Server.<br>
     * Adds it to permissions.yml file (read only).
     *
     * @param perm permission to register
     */
    public void registerPermission(@NotNull Permission perm) {
        registerPermission(perm, true);
    }

    /**
     * Generate a reload command for this plugin?<br>
     * <p>
     * If true on enable a command "/{pluginname}reload" is registered<br>
     * with permission "{pluginname}.command.reload" to use, by default for OP only.
     *
     * @return true if you wish to auto generate a reload command for this plugin
     * @see ReloadCommand
     */
    protected abstract boolean registerReloadCommand();

    /**
     * Register permission to the Server.<br>
     * Adds it to permissions.yml file (read only).
     *
     * @param perm   permission to register
     * @param silent false to notify console of newly registered permission
     */
    public void registerPermission(@NotNull Permission perm, boolean silent) {
        YMLConfig config = getConfig("permissions");
        config.options().pathSeparator('|');
        if (!perm.getDescription().isEmpty())
            if (!perm.getDescription().equals(config.get("permission|" + perm.getName() + "|description"))) {
                config.set("permissions|" + perm.getName() + "|description", perm.getDescription());
            }
        if (!perm.getChildren().isEmpty())
            if (!perm.getChildren().equals(config.get("permission|" + perm.getName() + "|children"))) {
                config.set("permissions|" + perm.getName() + "|children", perm.getChildren());
            }
        if (!perm.getDefault().toString().equalsIgnoreCase(config.getString("permission|" + perm.getName() + "|default"))) {
            config.set("permissions|" + perm.getName() + "|default", perm.getDefault().toString());
        }
        if (Bukkit.getPluginManager().getPermission(perm.getName()) == null)
            Bukkit.getPluginManager().addPermission(perm);
        else Bukkit.getPluginManager().recalculatePermissionDefaults(perm);
        registeredPermissions.add(perm.getName());
        if (!silent) logTetraStar(ChatColor.BLUE, "Registered permission " + ChatColor.YELLOW + perm.getName());
    }

    /**
     * Disable the plugin.<br>
     * Called by {@link #onDisable()}. <br>
     * Do NOT start async tasks here.
     */
    public abstract void disable();

    public @NotNull CooldownAPI getCooldownAPI() {
        return getCooldownAPI(true);
    }

    public @NotNull CooldownAPI getCooldownAPI(boolean persistent) {
        if (!persistent) {
            if (cooldownApi == null) cooldownApi = new CooldownAPI(null, false);
            return cooldownApi;
        }
        if (persistentCooldownApi == null) persistentCooldownApi = new CooldownAPI(this, true);
        return persistentCooldownApi;
    }

    /**
     * Enable the plugin. <br>
     * Called by {@link #onEnable()}.<br>
     * Should register Permissions here.<br>
     * Register Commands and Listeners here.<br>
     * May call {@link #onReload()}.<br>
     *
     * @see #registerPermission(Permission)
     * @see #registerCommand(Command)
     * @see #registerListener(Listener)
     */
    public abstract void enable();

    public @NotNull CounterAPI getCounterAPI(@NotNull CounterAPI.ResetTime reset) {
        if (!counterApiMap.containsKey(reset)) counterApiMap.put(reset, new CounterAPI(this, reset));
        return counterApiMap.get(reset);
    }

    /**
     * Load the plugin. <br>
     * Called by {@link #onLoad()}.<br>
     * Should initialize static instance of the plugin.<br>
     * Do NOT register Commands or Listeners here.<br>
     *
     * @see #registerPermission(Permission)
     */
    public abstract void load();

    public @NotNull CounterAPI getCounterAPI(@NotNull CounterAPI.ResetTime reset, boolean persistent) {
        if (!persistent) {
            if (!counterApiMap.containsKey(reset)) counterApiMap.put(reset, new CounterAPI(null, reset, false));
            return counterApiMap.get(reset);
        }
        if (!persistentCounterApiMap.containsKey(reset))
            persistentCounterApiMap.put(reset, new CounterAPI(this, reset, true));
        return persistentCounterApiMap.get(reset);
    }

    /**
     * logs on console message with plugin prefix
     *
     * @param log Message
     */
    public void log(String log) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + this.getName() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
    }

    public void logOnFile(String fileName, String text) {
        getLoggerManager().logText(fileName, text);
    }

    /**
     * Gets the LoggerManager of this plugin.
     *
     * @return plugin's LoggerManager.
     * @see #logOnFile(String, String)
     * @see #logOnFile(String, List)
     * @see #logOnFile(String, String[])
     */
    @Deprecated
    public LoggerManager getLoggerManager() {
        if (loggerManager == null) loggerManager = new LoggerManager(this);
        return loggerManager;
    }

    /**
     * Register a Command
     *
     * @param command Command to register
     * @return true if command is successfully registered
     * @see CoreCommand
     * @see #unregisterCommand(Command)
     */
    public boolean registerCommand(@NotNull Command command) {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            command.unregister(commandMap);
            if (!commandMap.register(this.getName().toLowerCase(Locale.ENGLISH), command))
                throw new IllegalArgumentException("Unable to register the command '" + command.getName() + "'");
            registeredCommands.add(command);
            logDone("Registered command " + ChatColor.YELLOW + "/" + command.getName() + ChatColor.WHITE + ", aliases: "
                    + ChatColor.YELLOW + Arrays.toString(command.getAliases().toArray()));
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            logProblem("Unable to register Command " + ChatColor.YELLOW + "/" + command.getName()
                    + ChatColor.WHITE + ", aliases: " + ChatColor.YELLOW +
                    Arrays.toString(command.getAliases().toArray()));
            log.error("Unexpected error ", e);
            return false;
        }
        return true;
    }

    public void logOnFile(String fileName, List<String> text) {
        getLoggerManager().logText(fileName, text);
    }

    /**
     * Unregister a Command
     *
     * @param command Command to unregister
     * @see #registerCommand(Command)
     */
    public void unregisterCommand(@NotNull Command command) {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            Map<String, Command> knownCommands = getKnownCommands(commandMap);
            List<String> keys = new ArrayList<>();
            for (String key : knownCommands.keySet()) {
                if (knownCommands.get(key).equals(command)) {
                    keys.add(key);
                }
            }
            for (String key : keys) {
                knownCommands.remove(key);
            }
            command.unregister(commandMap);
            registeredCommands.remove(command);
            logDone("Unregistered command " + ChatColor.YELLOW + "/" + command.getName() + ChatColor.WHITE + " for "
                    + this.getName() + ", aliases: " + ChatColor.YELLOW
                    + Arrays.toString(command.getAliases().toArray()));
        } catch (Throwable e) {
            logProblem("Unable to unregister command " + ChatColor.YELLOW + "/" + command.getName() + ChatColor.WHITE
                    + " for " + this.getName() + ", aliases: " + ChatColor.YELLOW
                    + Arrays.toString(command.getAliases().toArray()));
            log.error("Unexpected error ", e);
        }
    }

    public void logOnFile(String fileName, String[] text) {
        getLoggerManager().logText(fileName, text);
    }

    public SQLDatabase createConnection() throws ClassNotFoundException, SQLException {
        return createConnection(this.getConfig().loadString("database.database", "minecraft"));
    }

    public SQLDatabase createConnection(String databaseName) throws ClassNotFoundException, SQLException {
        String host = this.getConfig().loadString("database.host", "localhost");
        int port = this.getConfig().loadInteger("database.port", 3306);
        String username = this.getConfig().loadString("database.username", "root");
        String password = this.getConfig().loadString("database.password", "");
        return createConnection(databaseName, host, port, username, password);
    }

    /**
     * Gets plugin Config file.
     *
     * @return Plugin config file
     * @see #getConfig(String) getConfig("config.yml");
     */
    public @NotNull YMLConfig getConfig() {
        return getConfig("config.yml");
    }

    public SQLDatabase createConnection(String databaseName, String host, int port, String username, String password) throws ClassNotFoundException, SQLException {
        SQLType type = this.getConfig().loadEnum("database.type", SQLType.MYSQL, SQLType.class);
        return createConnection(type, databaseName, host, port, username, password);
    }

    /**
     * Gets config file.<br>
     * Also keep tracks of the file and reload it on {@link #onReload()} method
     * calls.<br>
     * Note: If you wish to not keep the file on memory or to not auto reload the file
     * on {@link #onReload()} method use {@link YMLConfig} constructor.<br>
     * Auto-append ".yml" to file name if not present.
     *
     * @param fileName might contains folder separator for file inside folders
     * @return config file at specified path inside plugin folder.
     */
    public @NotNull YMLConfig getConfig(@NotNull String fileName) {
        fileName = YMLConfig.fixName(fileName);
        if (configs.containsKey(fileName)) return configs.get(fileName);
        YMLConfig conf = new YMLConfig(this, fileName);
        configs.put(fileName, conf);
        return conf;
    }

    public SQLDatabase createConnection(SQLType type, String databaseName, String host, int port, String username, String password) throws ClassNotFoundException, SQLException {
        if (type == null) {
            logProblem("Invalid Database Type Connect");
            throw new SQLException();
        }
        logTetraStar(ChatColor.BLUE, "Connecting to " + ChatColor.YELLOW + type.getUrl(host, port, databaseName));

        try {
            SQLDatabase con = new SQLDatabase(type, host, username, password, databaseName, port);

            connections.add(con);

            logDone(type.name() + " successfully connected to &e" + con.getUrl());
            return con;
        } catch (Exception e) {
            logProblem(type.name() + " connection error: " + e.getMessage());
            throw e;
        }
    }

    public PacketManager getPacketManager() {
        if (packetManager == null) {
            packetManager = new PacketManager(this);
            this.logDone("PacketManager created hooked into ProtocolLib");
        }
        return packetManager;
    }

    protected void registerModule(@NotNull Module module) {
        modules.put(module.getID(), module);
        if (getConfig("modules.yml").loadBoolean(module.getID() + ".enable", true)) {
            enabledModules.add(module);//.put(module.getID(), true);
            this.registerListener(module);
            module.enable();
            this.logDone("Registered and &aenabled &fmodule &e" + module.getID());
        } else {
            //enabledModules.put(module.getID(), false);
            this.logDone("Registered as &cdisabled &fmodule &e" + module.getID());
            // HandlerList.unregisterAll(module);
        }
    }

    /**
     * Send to sender a text based on sender language.<br>
     * Message is not send if text is null or empty<br>
     * Shortcut for
     * sender.sendMessage({@link #loadLanguageMessage(CommandSender, String, String, String...)})
     *
     * @param sender Target of the message, also used for PAPI compatibility.
     * @param path   Path to get the message.
     * @param def    Default message
     * @param args   Holders and Replacers in the format
     *               ["holder#1","replacer#1","holder#2","replacer#2"...]
     * @see #getLanguageConfig(CommandSender)
     */
    @Deprecated
    public void sendLanguageMessage(CommandSender sender, String path, String def, String... args) {
        String msg = loadLanguageMessage(sender, path, def, args);
        if (msg != null && !msg.isEmpty()) sender.sendMessage(msg);
    }

    /**
     * Gets text based on sender language Shortcut for
     * {@link CorePlugin#getLanguageConfig(CommandSender)
     * getLanguageConfig(sender)}.{@link YMLConfig#loadString(String, String, org.bukkit.entity.Player, boolean, String...)
     * loadString(path, def, sender, true, args)}
     *
     * @param sender Target of the message, also used for PAPI compatibility.
     * @param path   Path to get the message.
     * @param def    Default message
     * @param args   Holders and Replacers in the format
     *               ["holder#1","replacer#1","holder#2","replacer#2"...]
     * @return Message based on sender language
     * @see #getLanguageConfig(CommandSender)
     */
    @Deprecated
    public @Nullable String loadLanguageMessage(@Nullable CommandSender sender, @NotNull String path, @Nullable String def, String... args) {
        return getLanguageConfig(sender).loadMessage(path, def, true, args);
    }

    public @NotNull Map<String, Module> getModules() {
        return Collections.unmodifiableMap(modules);
    }

    public boolean isActive(@NotNull Module module) {
        return enabledModules.contains(module);
    }

    public @Nullable Metrics setMetrics(@Nullable Integer bStatsId) {
        if (bStatsId == null) return null;
        try {
            return new Metrics(this, bStatsId);
        } catch (Exception e) {
            log.error("Unexpected error ", e);
            return null;
        }
    }

    public boolean setUpdateChecker(@Nullable Integer spigotResourceId) {
        if (spigotResourceId == null) return false;
        try {
            new UpdateChecker(this, spigotResourceId);
            return true;
        } catch (Exception e) {
            log.error("Unexpected error ", e);
            return false;
        }
    }


    public void setDebug(boolean value) {
        setDebug("standard", value);
    }

    public void setDebug(@NotNull String type, boolean value) {
        if (!UtilsString.isLowcasedValidID(type))
            throw new IllegalArgumentException();
        getConfig().set("debug." + type, value);
    }

    public boolean debug() {
        return debug("standard");
    }

    public boolean debug(@NotNull String type) {
        if (!UtilsString.isLowcasedValidID(type))
            throw new IllegalArgumentException();
        return getConfig().loadBoolean("debug." + type, false);
    }
}
