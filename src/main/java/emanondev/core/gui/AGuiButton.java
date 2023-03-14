package emanondev.core.gui;

import emanondev.core.UtilsMessages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AGuiButton implements GuiButton {

    private final Gui parent;

    public AGuiButton(@NotNull Gui parent) {
        this.parent = parent;
    }

    public @NotNull Gui getGui() {
        return parent;
    }

    /**
     * Notify sender the lack of permission as specified at
     * 'generic.lack_permission' path of language file
     *
     * @param target Notify target
     * @param perm   Permission to test
     */
    @Deprecated
    protected void permissionLackNotify(CommandSender target, Permission perm) {
        getPlugin().getLanguageConfig(target).loadMessage("generic.lack_permission",
                "&cYou lack of permission %permission%", "%permission%", perm.getName());
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     */
    @Deprecated
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, String def,
                                       boolean color, CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, loadMessage(receiver, path, def, color, target, holders));
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, String def, boolean color,
                                 CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("gui_button." + path, def, color,
                target, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     */
    @Deprecated
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def,
                                       boolean color, CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, loadMessage(receiver, path, def, color, target, holders));
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, List<String> def,
                                 boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("gui_button." + path, def, color,
                target, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     */
    @Deprecated
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, String def,
                                       boolean color, String... holders) {
        sendMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     */
    @Deprecated
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def,
                                       boolean color, String... holders) {
        sendMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     */
    @Deprecated
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, String def,
                                       String... holders) {
        sendMessageFeedback(receiver, path, def, true, receiver, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     */
    @Deprecated
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def,
                                       String... holders) {
        sendMessageFeedback(receiver, path, def, true, receiver, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     */
    @Deprecated
    protected void sendMessageFeedback(CommandSender receiver, @NotNull String path, String def,
                                       CommandSender target, String... holders) {
        sendMessageFeedback(receiver, path, def, true, target, holders);
    }

    /**
     * Sends a message to receiver
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     */
    @Deprecated
    protected void sendMultiMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def,
                                            CommandSender target, String... holders) {
        sendMessageFeedback(receiver, path, def, true, target, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, String def, boolean color,
                                 String... holders) {
        return loadMessage(receiver, path, def, color, receiver, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, List<String> def,
                                 boolean color, String... holders) {
        return loadMessage(receiver, path, def, color, receiver, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, String def,
                                 String... holders) {
        return loadMessage(receiver, path, def, true, receiver, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, List<String> def,
                                 String... holders) {
        return loadMessage(receiver, path, def, true, receiver, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, String def,
                                 CommandSender target, String... holders) {
        return loadMessage(receiver, path, def, true, target, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected String loadMessage(CommandSender receiver, @NotNull String path, List<String> def,
                                 CommandSender target, String... holders) {
        return loadMessage(receiver, path, def, true, target, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver   Message receiver
     * @param path       final configuration path is
     *                   <b>'gui_button.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param color      Whether translate color codes or not
     * @param target     Player target for PlaceHolderAPI holders
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                    String defMessage, String defHover, String defClick, ClickEvent.Action action,
                                                    boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadComponentMessage("gui_button." + path,
                defMessage, defHover, defClick, action, color, target, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver   Message receiver
     * @param path       final configuration path is
     *                   <b>'gui_button.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param color      Whether translate color codes or not
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                    String defMessage, String defHover, String defClick, ClickEvent.Action action,
                                                    boolean color, String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, action, color, receiver, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver   Message receiver
     * @param path       final configuration path is
     *                   <b>'gui_button.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param target     Player target for PlaceHolderAPI holders
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                    String defMessage, String defHover, String defClick, ClickEvent.Action action,
                                                    CommandSender target, String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, action, true, target, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver   Message receiver
     * @param path       final configuration path is
     *                   <b>'gui_button.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                    String defMessage, String defHover, String defClick, ClickEvent.Action action,
                                                    String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, action, true, receiver, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver   Message receiver
     * @param path       final configuration path is
     *                   <b>'gui_button.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param color      Whether translate color codes or not
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                    String defMessage, String defHover, String defClick, boolean color,
                                                    String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, null, color, receiver, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver   Message receiver
     * @param path       final configuration path is
     *                   <b>'gui_button.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param target     Player target for PlaceHolderAPI holders
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                    String defMessage, String defHover, String defClick,
                                                    CommandSender target, String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, null, true, target, holders);
    }

    /**
     * Get message and set default if absent
     *
     * @param receiver   Message receiver
     * @param path       final configuration path is
     *                   <b>'gui_button.'+path</b>
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected ComponentBuilder loadComponentMessage(@NotNull CommandSender receiver, @NotNull String path,
                                                    String defMessage, String defHover, String defClick, String... holders) {
        return loadComponentMessage(receiver, path, defMessage, defHover, defClick, null, true, receiver, holders);
    }

    /**
     * Get message list and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected List<String> loadMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                            List<String> def, boolean color, String... holders) {
        return loadMultiMessage(receiver, path, def, color, receiver, holders);
    }

    /**
     * Get message list and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected List<String> loadMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                            List<String> def, CommandSender target, String... holders) {
        return loadMultiMessage(receiver, path, def, true, target, holders);
    }

    /**
     * Get message list and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected List<String> loadMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                            List<String> def, String... holders) {
        return loadMultiMessage(receiver, path, def, true, receiver, holders);
    }

    /**
     * Get message list and set default if absent
     *
     * @param receiver Message receiver
     * @param path     final configuration path is
     *                 <b>'gui_button.'+path</b>
     * @param def      Default message
     * @param color    Whether translate color codes or not
     * @param target   Player target for PlaceHolderAPI holders
     * @param holders  Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return message list and set default if absent
     */
    @Deprecated
    protected List<String> loadMultiMessage(@NotNull CommandSender receiver, @NotNull String path,
                                            List<String> def, boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMultiMessage("gui_button." + path, def,
                color, receiver, holders);
    }


    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, String def, boolean color, CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, getMessage(receiver, path, def, color, target, holders));
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, String def, boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("gui_button." + path,
                def, color, target, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def, boolean color, CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, getPlugin().getLanguageConfig(receiver).loadMessage("gui_button." + path,
                def, color, target, holders));
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, List<String> def, boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("gui_button." + path,
                def, color, target, holders);
    }


    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, String def, boolean color, String... holders) {
        giveMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def, boolean color, String... holders) {
        giveMessageFeedback(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, String def, String... holders) {
        giveMessageFeedback(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def, String... holders) {
        giveMessageFeedback(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, String def, CommandSender target, String... holders) {
        giveMessageFeedback(receiver, path, def, true, target, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def, CommandSender target, String... holders) {
        giveMessageFeedback(receiver, path, def, true, target, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, String def, boolean color, String... holders) {
        return getMessage(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, List<String> def, boolean color, String... holders) {
        return getMessage(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, String def, String... holders) {
        return getMessage(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, List<String> def, String... holders) {
        return getMessage(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, String def, CommandSender target, String... holders) {
        return getMessage(receiver, path, def, true, target, holders);
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, List<String> def, CommandSender target, String... holders) {
        return getMessage(receiver, path, def, true, target, holders);
    }

    @Deprecated
    protected ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path, String defMessage, String defHover, String defClick, ClickEvent.Action action, boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadComponentMessage("gui_button." + path, defMessage, defHover, defClick, action, color, target, holders);
    }

    @Deprecated
    protected ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path, String defMessage, String defHover, String defClick, ClickEvent.Action action, boolean color, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, action, color, receiver, holders);
    }

    @Deprecated
    protected ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path, String defMessage, String defHover, String defClick, ClickEvent.Action action, CommandSender target, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, action, true, target, holders);
    }

    @Deprecated
    protected ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path, String defMessage, String defHover, String defClick, ClickEvent.Action action, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, action, true, receiver, holders);
    }

    @Deprecated
    protected ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path, String defMessage, String defHover, String defClick, boolean color, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, null, color, receiver, holders);
    }

    @Deprecated
    protected ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path, String defMessage, String defHover, String defClick, CommandSender target, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, null, true, target, holders);
    }

    @Deprecated
    protected ComponentBuilder getComponent(@NotNull CommandSender receiver, @NotNull String path, String defMessage, String defHover, String defClick, String... holders) {
        return getComponent(receiver, path, defMessage, defHover, defClick, null, true, receiver, holders);
    }

    @Deprecated
    protected List<String> getMultiMessage(@NotNull CommandSender receiver, @NotNull String path, List<String> def, boolean color, String... holders) {
        return getMultiMessage(receiver, path, def, color, receiver, holders);
    }

    @Deprecated
    protected List<String> getMultiMessage(@NotNull CommandSender receiver, @NotNull String path, List<String> def, CommandSender target, String... holders) {
        return getMultiMessage(receiver, path, def, true, target, holders);
    }

    @Deprecated
    protected List<String> getMultiMessage(@NotNull CommandSender receiver, @NotNull String path, List<String> def, String... holders) {
        return getMultiMessage(receiver, path, def, true, receiver, holders);
    }

    @Deprecated
    protected List<String> getMultiMessage(@NotNull CommandSender receiver, @NotNull String path, List<String> def, boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMultiMessage("gui_button." + path, def, color, receiver, holders);
    }

}