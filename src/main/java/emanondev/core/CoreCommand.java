package emanondev.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import emanondev.core.util.CompleteUtility;
import emanondev.core.util.ConsoleLogger;
import emanondev.core.util.ReadUtility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class CoreCommand extends Command implements PluginIdentifiableCommand, CompleteUtility, ReadUtility, ConsoleLogger {

    private final CorePlugin plugin;
    private final YMLConfig config;
    private final String id;

    private final Permission permission;

    /**
     * Returns the ID of the Command.
     *
     * @return Command ID.
     */
    public String getID() {
        return id;
    }

    /**
     * Returns config file of this Command.
     *
     * @return Config file of this Command.
     */
    public YMLConfig getConfig() {
        return config;
    }

    /**
     * Returns the owner of this PluginIdentifiableCommand.
     *
     * @return Plugin that owns this Command.
     */
    @Override
    public @NotNull CorePlugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the permission associated to the command.
     *
     * @return the permission associated to the command
     */
    public @Nullable Permission getCommandPermission() {
        return permission;
    }

    /**
     * Construct a new Command. Note: id is used for both config file of the command
     * and default command name.
     *
     * @param id                 Command ID.
     * @param plugin             Plugin that own this Command.
     * @param permission         Permission required to use this Command if exists.
     * @param defaultDescription Default description of the Command, might be updated on Command
     *                           file.
     */
    public CoreCommand(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission,
                       @Nullable String defaultDescription) {
        this(id, plugin, permission, defaultDescription, null);
    }

    /**
     * Construct a new Command. Note: id is used for both config file of the command
     * and default command name.
     *
     * @param id                 Command ID.
     * @param plugin             Plugin that own this Command.
     * @param permission         Permission required to use this Command if exists.
     * @param defaultDescription Default description of the Command, might be updated on Command
     *                           file.
     * @param defaultAliases     Default aliases of the Command, might be changed on Command file
     *                           and applied with Server restart or Plugin restart.
     */
    public CoreCommand(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission,
                       @Nullable String defaultDescription, @Nullable List<String> defaultAliases) {
        super(plugin.getConfig("Commands/" + id.toLowerCase()).loadString("info.name", id.toLowerCase()).toLowerCase());
        if (id.isEmpty() || id.contains(" "))
            throw new IllegalArgumentException("Invalid id");
        this.id = id.toLowerCase();

        this.plugin = plugin;
        this.config = this.plugin.getConfig("Commands/" + this.id);
        this.permission = permission;
        List<String> tempAliases = new ArrayList<>();
        for (String alias : config.loadStringList("info.aliases",
                defaultAliases == null ? Collections.emptyList() : defaultAliases)) {
            if (alias == null)
                new NullPointerException("Null alias while creating command '" + id + "' (" + getName() + ")")
                        .printStackTrace();
            else if (alias.contains(" "))
                new IllegalArgumentException(
                        "Invalid alias '" + alias + "' while creating command '" + id + "' (" + getName() + ")")
                        .printStackTrace();
            else if (tempAliases.contains(alias.toLowerCase()))
                new IllegalArgumentException(
                        "Alias used twice '" + alias + "' while creating command '" + id + "' (" + getName() + ")")
                        .printStackTrace();
            else
                tempAliases.add(alias.toLowerCase());
        }
        this.setAliases(tempAliases);
        this.setDescription(config.loadMessage("info.description", defaultDescription==null?"":defaultDescription, true));
        this.setUsage(config.loadMessage("info.usage", "&cUsage: /" + getName(), true));
        if (permission != null) {
            // plugin.registerPermission(permission);
            this.setPermission(permission.getName());
            this.setPermissionMessage(
                    config.loadMessage("info.permission-message", getDefaultPermissionMessage(), true));
        }
    }

    /**
     * Override this only if you wish to change message shown on command fail used
     * as default<br>
     * Note: default is used only if the config file of the command has no value
     * set for this message
     *
     * @return defaultPermissionMessage
     */
    protected String getDefaultPermissionMessage() {
        return permission == null ? (ChatColor.RED + "You lack of permissions")
                : (ChatColor.RED + "You lack of permission " + permission.getName());
    }

    /**
     * Execute the command.
     *
     * @param sender Source object which is executing this command
     * @param alias  The alias of the command used
     * @param args   All arguments passed to the command, split via ' '
     * @return true
     */
    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (this.getCommandPermission() != null && !sender.hasPermission(this.getCommandPermission())) {
            this.permissionLackNotify(sender, this.getCommandPermission());
            return true;
        }
        onExecute(sender, alias, args);
        return true;
    }

    /**
     * Execute the command.
     *
     * @param sender Source object which is executing this command
     * @param alias  The alias of the command used
     * @param args   All arguments passed to the command, split via ' '
     */
    public abstract void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args);

    /**
     * Executed on tab completion for this command, returning a list of options the
     * player can tab through.
     *
     * @param sender   Source object which is executing this command
     * @param alias    the alias being used
     * @param args     All arguments passed to the command, split via ' '
     * @param location The position looked at by the sender, or null if none
     * @return a list of tab-completions for the specified arguments. This will
     * never be null. List may be immutable.
     * @throws IllegalArgumentException if sender, alias, or args is null
     */
    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args,
                                                   @Nullable Location location) {
        if (args == null)
            throw new NullPointerException();
        List<String> val = onComplete(sender, alias, args, location);
        return val == null ? new ArrayList<>() : val;
    }

    /**
     * Executed on tab completion for this command, returning a list of options the
     * player can tab through.
     *
     * @param sender Source object which is executing this command
     * @param alias  the alias being used
     * @param args   All arguments passed to the command, split via ' '
     * @return a list of tab-completions for the specified arguments. This will
     * never be null. List may be immutable.
     * @throws IllegalArgumentException if sender, alias, or args is null
     */
    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                                   @NotNull String[] args) {
        Validate.isTrue(args != null);
        return onComplete(sender, alias, args, null);
    }

    /**
     * Executed on tab completion for this command, returning a list of options the
     * player can tab through.
     *
     * @param sender Source object which is executing this command
     * @param alias  the alias being used
     * @param args   All arguments passed to the command, split via ' '
     * @param loc    sender location
     * @return a list of tab-completions for the specified arguments. May be null.
     * @throws IllegalArgumentException if sender, alias, or args is null
     */
    public abstract List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias,
                                            @NotNull String[] args, @Nullable Location loc);

    /**
     * @param sender     the sender
     * @param permission the permission
     * @return true if permission is null or sender has permission
     */
    protected boolean hasPermission(@NotNull Permissible sender, @Nullable Permission permission) {
        if (permission == null)
            return true;
        return sender.hasPermission(permission);
    }

    /**
     * @param sender     the sender
     * @param permission the permission
     * @return true if permission is null or sender has permission
     * @see #hasPermission(Permissible, Permission)
     */
    @Deprecated
    protected boolean hasPermission(@NotNull Permissible sender, @Nullable String permission) {
        if (permission == null)
            return true;
        return sender.hasPermission(permission);
    }

    public void log(String log) {
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&',
                        ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + getPlugin().getName() + ChatColor.DARK_BLUE + "|Command "
                                + ChatColor.WHITE + getID() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
    }

    /**
     * Notify sender the lack of permission as specified at
     * 'generic.lack_permission' path of language file
     *
     * @param sender target to notify
     * @param perm   lacking permission
     */
    protected void permissionLackNotify(@NotNull CommandSender sender, @NotNull Permission perm) {
        getLanguageSection(sender).loadMessage("lack_permission",
                "&cYou lack of permission %permission%", "%permission%", perm.getName());
    }

    /**
     * Notify sender the command may be used by players only
     *
     * @param sender who
     */
    protected void playerOnlyNotify(CommandSender sender) {
        getLanguageSection(sender).loadMessage("players_only", "&cCommand for players only");
    }

    /**
     * Returns language section for the command sender<br>
     * (load language config and go to sub pattern 'command.[command id]')
     *
     * @param who target user
     * @return language section for the command sender
     */
    public @NotNull YMLSection getLanguageSection(@Nullable CommandSender who) {
        return getPlugin().getLanguageConfig(who).loadSection("command." + this.getID());
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       boolean color, @Nullable CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, loadMessage(receiver, path, def, color, target, holders));
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       boolean color, String... holders) {
        sendMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       boolean color, String... holders) {
        sendMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       String... holders) {
        sendMessageFeedback(receiver, path, def, true, receiver, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       String... holders) {
        sendMessageFeedback(receiver, path, def, true, receiver, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       @Nullable CommandSender target, String... holders) {
        sendMessageFeedback(receiver, path, def, true, target, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMultiMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                            @Nullable CommandSender target, String... holders) {
        sendMessageFeedback(receiver, path, def, true, target, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       boolean color, @Nullable CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, loadMessage(receiver, path, def, color, target, holders));
    }

    /**
     * Gets text based on sender language.<br>
     * Shortcut for
     * {@link #getPlugin()}.{@link CorePlugin#getLanguageConfig(CommandSender)
     * getLanguageConfig(sender)}.{@link YMLConfig#loadString(String, String, org.bukkit.entity.Player, boolean, String...)
     * loadString(path, def, sender, true, args)}
     *
     * @param sender Target of the message, also used for PAPI compatibility.
     * @param path   Path to get the message.
     * @param def    Default message
     * @param args   Holders and Replacers in the format
     *               ["holder#1","replacer#1","holder#2","replacer#2"...]
     * @return Message based on sender language
     */
    @Deprecated
    public @Nullable String loadLanguageMessage(@Nullable CommandSender sender, @NotNull String path,
                                                @Nullable String def, String... args) {
        return getPlugin().getLanguageConfig(sender).loadMessage(path, def, true, args);
    }

    /**
     * Send to sender a text based on sender language.<br>
     * Don't send message if text is null or empty<br>
     * Shortcut for
     * sender.sendMessage({@link #loadLanguageMessage(CommandSender, String, String, String...)
     * loadLanguageMessage(sender, path, def, args)})
     *
     * @param sender Target of the message, also used for PAPI compatibility.
     * @param path   Path to get the message.
     * @param def    Default message
     * @param args   Holders and Replacers in the format
     *               ["holder#1","replacer#1","holder#2","replacer#2"...]
     */
    @Deprecated
    public void sendLanguageMessage(CommandSender sender, String path, String def, String... args) {
        String msg = loadLanguageMessage(sender, path, def, args);
        if (msg != null && !msg.isEmpty())
            sender.sendMessage(msg);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, @Nullable String def, boolean color,
                                 @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("command." + this.getID() + "." + path, def, color,
                target, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                 boolean color, @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("command." + this.getID() + "." + path, def, color,
                target, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, @Nullable String def, boolean color,
                                 String... holders) {
        return loadMessage(receiver, path, def, color, receiver, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                 boolean color, String... holders) {
        return loadMessage(receiver, path, def, color, receiver, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, @Nullable String def,
                                 String... holders) {
        return loadMessage(receiver, path, def, true, receiver, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                 String... holders) {
        return loadMessage(receiver, path, def, true, receiver, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, @Nullable String def,
                                 @Nullable CommandSender target, String... holders) {
        return loadMessage(receiver, path, def, true, target, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                 @Nullable CommandSender target, String... holders) {
        return loadMessage(receiver, path, def, true, target, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver   message target
     * @param path       final configuration path is
     *                   <b>'command.'+this.getID()+'.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param color      Whether translate color codes or not
     * @param target     Player target for PlaceHolderAPI holders
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                              @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
                                                              boolean color, @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadComponentMessage("command." + this.getID() + "." + path,
                defMessage, defHover, defClick, action, color, target, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver   message target
     * @param path       final configuration path is
     *                   <b>'command.'+this.getID()+'.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param color      Whether translate color codes or not
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                              @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
                                                              boolean color, String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, action, color, receiver, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver   message target
     * @param path       final configuration path is
     *                   <b>'command.'+this.getID()+'.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param target     Player target for PlaceHolderAPI holders
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                              @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
                                                              @Nullable CommandSender target, String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, action, true, target, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver   message target
     * @param path       final configuration path is
     *                   <b>'command.'+this.getID()+'.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                              @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
                                                              String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, action, true, receiver, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver   message target
     * @param path       final configuration path is
     *                   <b>'command.'+this.getID()+'.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param color      Whether translate color codes or not
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                              @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, boolean color,
                                                              String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, null, color, receiver, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver   message target
     * @param path       final configuration path is
     *                   <b>'command.'+this.getID()+'.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param target     Player target for PlaceHolderAPI holders
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                              @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick,
                                                              @Nullable CommandSender target, String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, null, true, target, holders);
    }

    /**
     * Returns message and set default if absent
     *
     * @param receiver   message target
     * @param path       final configuration path is
     *                   <b>'command.'+this.getID()+'.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                              @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, null, true, receiver, holders);
    }

    /**
     * Get message list and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable List<String> loadMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable List<String> def, boolean color, String... holders) {
        return loadMultiMessage(receiver, path, def, color, receiver, holders);
    }

    /**
     * Get message list and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message list and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable List<String> loadMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable List<String> def, @Nullable CommandSender target, String... holders) {
        return loadMultiMessage(receiver, path, def, true, target, holders);
    }

    /**
     * Get message list and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message list and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable List<String> loadMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable List<String> def, String... holders) {
        return loadMultiMessage(receiver, path, def, true, receiver, holders);
    }

    /**
     * Get message list and set default if absent
     *
     * @param receiver message target
     * @param path     final configuration path is
     *                 <b>'command.'+this.getID()+'.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     * @return message list and set default if absent
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    protected @Nullable List<String> loadMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMultiMessage("command." + this.getID() + "." + path, def,
                color, target, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       boolean color, @Nullable CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, getMessage(receiver, path, def, color, target, holders));
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable String def, boolean color,
                                @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("command." + this.getID() + "." + path, def, color,
                target, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       boolean color, @Nullable CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, getPlugin().getLanguageConfig(receiver)
                .loadMessage("command." + this.getID() + "." + path, def, color, target, holders));
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def, boolean color,
                                @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("command." + this.getID() + "." + path, def, color,
                target, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       boolean color, String... holders) {
        giveMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       boolean color, String... holders) {
        giveMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       String... holders) {
        giveMessageFeedback(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       String... holders) {
        giveMessageFeedback(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       @Nullable CommandSender target, String... holders) {
        giveMessageFeedback(receiver, path, def, true, target, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       @Nullable CommandSender target, String... holders) {
        giveMessageFeedback(receiver, path, def, true, target, holders);
    }

    @Deprecated

    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable String def, boolean color,
                                String... holders) {
        return getMessage(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def, boolean color,
                                String... holders) {
        return getMessage(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable String def, String... holders) {
        return getMessage(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                String... holders) {
        return getMessage(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable String def,
                                @Nullable CommandSender target, String... holders) {
        return getMessage(receiver, path, def, true, target, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                @Nullable CommandSender target, String... holders) {
        return getMessage(receiver, path, def, true, target, holders);
    }

    @Deprecated

    protected @Nullable ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
                                                      boolean color, @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("command." + this.getID() + "." + path, defMessage,
                defHover, defClick, action, color, target, holders);
    }

    @Deprecated
    protected @Nullable ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
                                                      boolean color, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, action, color, receiver, holders);
    }

    @Deprecated
    protected @Nullable ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
                                                      @Nullable CommandSender target, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, action, true, target, holders);
    }

    @Deprecated
    protected @Nullable ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
                                                      String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, action, true, receiver, holders);
    }

    @Deprecated
    protected @Nullable ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, boolean color,
                                                      String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, null, color, receiver, holders);
    }

    @Deprecated
    protected @Nullable ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick,
                                                      @Nullable CommandSender target, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, null, true, target, holders);
    }

    @Deprecated
    protected @Nullable ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path,
                                                      @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, null, true, receiver, holders);
    }

    @Deprecated
    protected @Nullable List<String> getMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                     @Nullable List<String> def, boolean color, String... holders) {
        return getMultiMessage(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected @Nullable List<String> getMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                     @Nullable List<String> def, @Nullable CommandSender target, String... holders) {
        return getMultiMessage(receiver, path, def, true, target, holders);
    }

    @Deprecated
    protected @Nullable List<String> getMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                     @Nullable List<String> def, String... holders) {
        return getMultiMessage(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected @Nullable List<String> getMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                     @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMultiMessage("command." + this.getID() + "." + path, def,
                color, target, holders);
    }

}
