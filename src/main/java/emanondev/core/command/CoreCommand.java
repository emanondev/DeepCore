package emanondev.core.command;

import emanondev.core.*;
import emanondev.core.message.DMessage;
import emanondev.core.util.FileLogger;
import emanondev.core.utility.CompletionHelper;
import emanondev.core.utility.ConsoleHelper;
import emanondev.core.utility.ReadHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public abstract class CoreCommand extends Command implements PluginIdentifiableCommand, CompletionHelper, ReadHelper, ConsoleHelper, FileLogger {

    private final CorePlugin plugin;
    private final YMLConfig config;
    private final String id;

    private final Permission permission;

    /**
     * Construct a new Command. Note: id is used for both config file of the command
     * and default command name.
     *
     * @param id         Command ID.
     * @param plugin     Plugin that own this Command.
     * @param permission Permission required to use this Command if exists.
     */
    public CoreCommand(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission) {
        this(id, plugin, permission, null, null);
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
        super(plugin.getConfig("Commands/" + id.toLowerCase(Locale.ENGLISH)).loadString("info.name", id.toLowerCase(Locale.ENGLISH)).toLowerCase(Locale.ENGLISH));
        if (id.isEmpty() || id.contains(" "))
            throw new IllegalArgumentException("Invalid id");
        this.id = id.toLowerCase(Locale.ENGLISH);

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
            else if (tempAliases.contains(alias.toLowerCase(Locale.ENGLISH)))
                new IllegalArgumentException(
                        "Alias used twice '" + alias + "' while creating command '" + id + "' (" + getName() + ")")
                        .printStackTrace();
            else
                tempAliases.add(alias.toLowerCase(Locale.ENGLISH));
        }
        this.setAliases(tempAliases);
        this.setDescription(config.loadMessage("info.description", defaultDescription == null ? "" : defaultDescription, true));
        this.setUsage(config.loadMessage("info.usage", ChatColor.RED + "Usage: /" + getName(), true));
        if (permission != null) {
            // plugin.registerPermission(permission);
            this.setPermission(permission.getName());
            this.setPermissionMessage(
                    config.loadMessage("info.permission-message", getDefaultPermissionMessage(), true));
        }
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
     * Returns config file of this Command.
     *
     * @return Config file of this Command.
     */
    public @NotNull YMLConfig getConfig() {
        return config;
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
     * Returns the permission associated to the command.
     *
     * @return the permission associated to the command
     */
    public @Nullable Permission getCommandPermission() {
        return permission;
    }

    /**
     * Notify sender the lack of permission as specified at
     * 'generic.lack_permission' path of language file
     *
     * @param sender target to notify
     * @param perm   lacking permission
     */
    protected void permissionLackNotify(@NotNull CommandSender sender, @NotNull Permission perm) {
        new DMessage(CoreMain.get(), sender).appendLang("command.lack_permission",
                "%permission%", perm.getName(), "%plugin%", getPlugin().getName()).send();
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
     * Returns the owner of this PluginIdentifiableCommand.
     *
     * @return Plugin that owns this Command.
     */
    @Override
    public @NotNull CorePlugin getPlugin() {
        return plugin;
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
        return tabComplete(sender, alias, args, null);
    }

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
        return val == null ? Collections.emptyList() : val;
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
    public abstract @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias,
                                                      @NotNull String[] args, @Nullable Location loc);

    /**
     * Executed on tab completion for this command, returning a list of options the
     * player can tab through.
     *
     * @param sender Source object which is executing this command
     * @param alias  the alias being used
     * @param args   All arguments passed to the command, split via ' '
     * @return a list of tab-completions for the specified arguments. May be null.
     * @throws IllegalArgumentException if sender, alias, or args is null
     */
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias,
                                             @NotNull String[] args) {
        return onComplete(sender, alias, args, null);
    }

    /**
     * @param sender     the sender
     * @param permission the permission
     * @return true if permission is null or sender has permission
     */
    @Contract("_, null -> true")
    protected boolean hasPermission(@NotNull Permissible sender, @Nullable Permission permission) {
        return permission == null || sender.hasPermission(permission);
    }

    /**
     * @param sender     the sender
     * @param permission the permission
     * @return true if permission is null or sender has permission
     * @see #hasPermission(Permissible, Permission)
     */
    @Deprecated
    @Contract("_, null -> true")
    protected boolean hasPermission(@NotNull Permissible sender, @Nullable String permission) {
        return permission == null || sender.hasPermission(permission);
    }

    @Override
    public void log(String log) {
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&',
                        ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + getPlugin().getName() + ChatColor.DARK_BLUE + "|Command "
                                + ChatColor.WHITE + getID() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
    }

    /**
     * Returns the ID of the Command.
     *
     * @return Command ID.
     */
    public final @NotNull String getID() {
        return id;
    }

    /**
     * Notify sender the command may be used by players only
     *
     * @param sender who
     */
    protected void playerOnlyNotify(CommandSender sender) {
        new DMessage(CoreMain.get(), sender).appendLang("command.players_only",
                "%plugin%", getPlugin().getName()).send();
    }

    public @NotNull String getLanguagePath(@NotNull String subPath) {
        return "command." + this.getID() + "." + subPath;
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
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       boolean color, @Nullable CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver,
                getPlugin().getLanguageConfig(receiver).loadMessage("command." + this.getID() + "." + path, def, color,
                        target, holders));
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
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
     */
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       boolean color, @Nullable CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, getLanguageSection(receiver).loadMessage(path, def, color, target, holders));
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

    public void reload() {
    }


    /**
     * Returns language section for this command.
     *
     * @param sender language target
     * @return language section for this command.
     * @see #getLanguageSection(CommandSender)
     */
    @Deprecated
    public YMLSection getCommandLang(@Nullable CommandSender sender) {
        return getPlugin().getLanguageConfig(sender).loadSection(getPathLang());
    }


    /**
     * Returns language section for selected sub command.
     *
     * @param sender language target
     * @param subId  sub command id
     * @return language section for selected sub command.
     */
    @Deprecated
    public YMLSection getSubCommandLang(@Nullable CommandSender sender, @NotNull String subId) {
        return getCommandLang(sender).loadSection(getPathSubCommandLang(subId));
    }

    protected @NotNull String getPathErrorLang() {
        return getPathLang("error");
    }

    protected @NotNull String getPathLang(@NotNull String subPath) {
        return getPathLang() + "." + subPath;
    }

    @Deprecated
    protected @NotNull String getPathLang() {
        return "command." + getID();
    }

    protected @NotNull String getPathSubCommandLang(@NotNull String subId) {
        return getPathLang("subCommand." + subId);
    }

    protected @NotNull String getPathSubCommandLang(@NotNull String subId, String subPath) {
        return getPathLang("subCommand." + subId + "." + subPath);
    }

}
