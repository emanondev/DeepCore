package emanondev.core;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @see emanondev.core.command.CoreCommand
 */
@Deprecated
public abstract class CoreCommand extends emanondev.core.command.CoreCommand {

    /**
     * Construct a new Command. Note: id is used for both config file of the command
     * and default command name.
     *
     * @param id         Command ID.
     * @param plugin     Plugin that own this Command.
     * @param permission Permission required to use this Command if exists.
     * @see emanondev.core.command.CoreCommand
     */
    @Deprecated
    public CoreCommand(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission) {
        super(id, plugin, permission);
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
     * @see emanondev.core.command.CoreCommand
     */
    @Deprecated
    public CoreCommand(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission,
                       @Nullable String defaultDescription) {
        super(id, plugin, permission, defaultDescription);
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
     * @see emanondev.core.command.CoreCommand
     */
    @Deprecated
    public CoreCommand(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission,
                       @Nullable String defaultDescription, @Nullable List<String> defaultAliases) {
        super(id, plugin, permission, defaultDescription, defaultAliases);
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

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable String def,
                                       boolean color, String... holders) {
        giveMessageFeedback(receiver, path, def, color, receiver, holders);
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
                                       boolean color, String... holders) {
        giveMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, @Nullable List<String> def,
                                       boolean color, @Nullable CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, getPlugin().getLanguageConfig(receiver)
                .loadMessage("command." + this.getID() + "." + path, def, color, target, holders));
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
    protected String getMessage(CommandSender receiver, @NotNull String path, @Nullable List<String> def, boolean color,
                                @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("command." + this.getID() + "." + path, def, color,
                target, holders);
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
                                                      boolean color, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, action, color, receiver, holders);
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
                                                     @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMultiMessage("command." + this.getID() + "." + path, def,
                color, target, holders);
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

}
