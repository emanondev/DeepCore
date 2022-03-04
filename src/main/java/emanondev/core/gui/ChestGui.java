package emanondev.core.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import emanondev.core.CorePlugin;
import emanondev.core.UtilsMessages;
import emanondev.core.UtilsString;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;


public abstract class ChestGui implements Gui {

    private final Gui previousHolder;
    private final Player player;
    private final Inventory inv;
    private final CorePlugin plugin;
    private boolean updateOnOpen = true;

    /**
     * Create a chest-type gui
     *
     * @param title          the raw title, may be null
     * @param rows           amount of rows [1:9]
     * @param player         target player, may be null
     * @param previousHolder previously used gui, may be null
     * @param plugin         the plugin responsible for this gui
     */
    public ChestGui(String title, int rows, Player player, Gui previousHolder, @NotNull CorePlugin plugin) {
        this(title, rows, player, previousHolder, plugin, false);
    }

    /**
     * Create a chest-type gui
     *
     * @param title          the raw title, may be null
     * @param rows           amount of rows [1:9]
     * @param player         target player, may be null
     * @param previousHolder previously used gui, may be null
     * @param plugin         the plugin responsible for this gui
     * @param isTimerUpdated update the inventory each seconds as long as it has at least a player
     */
    public ChestGui(String title, int rows, Player player, Gui previousHolder, @NotNull CorePlugin plugin, boolean isTimerUpdated) {
        if (rows < 1 || rows > 9)
            throw new IllegalArgumentException("invalid rows size '" + rows + "'");
        if (plugin == null)
            throw new NullPointerException();
        this.previousHolder = previousHolder;
        this.player = player;
        this.inv = Bukkit.createInventory(this, rows * 9, UtilsString.fix(title, player, true));
        this.plugin = plugin;
        if (isTimerUpdated)
            GuiHandler.registerTimerUpdatedGui(this);
    }

    public @NotNull CorePlugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    @Override
    public final Player getTargetPlayer() {
        return player;
    }

    @Override
    public Gui getPreviousGui() {
        return previousHolder;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    /**
     * Sets whenever the inventory should be updated when a player open it
     *
     * @param value
     */
    public void setUpdateOnOpen(boolean value) {
        this.updateOnOpen = value;
    }

    /**
     * @return true if the inventory is updated when a player open it
     */
    public boolean isUpdateOnOpen() {
        return this.updateOnOpen;
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, String def, boolean color, CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, getMessage(receiver, path, def, color, target, holders));
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, String def, boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("gui." + path,
                def, color, target, holders);
    }

    @Deprecated
    protected void giveMessageFeedback(CommandSender receiver, @NotNull String path, List<String> def, boolean color, CommandSender target, String... holders) {
        UtilsMessages.sendMessage(receiver, getPlugin().getLanguageConfig(receiver).loadMessage("gui." + path,
                def, color, target, holders));
    }

    @Deprecated
    protected String getMessage(CommandSender receiver, @NotNull String path, List<String> def, boolean color, CommandSender target, String... holders) {
        return getPlugin().getLanguageConfig(receiver).loadMessage("gui." + path,
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
        return getPlugin().getLanguageConfig(receiver).loadMessage("gui." + path, defMessage, defHover, defClick, action, color, target, holders);
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
        return getPlugin().getLanguageConfig(receiver).loadMultiMessage("gui." + path, def, color, receiver, holders);
    }

}
