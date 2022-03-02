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

	private final Gui previusHolder;
	private final Player player;
	private final Inventory inv;
	private final CorePlugin plugin;
	private boolean updateOnOpen = true;

	/**
	 * Create a chesttype gui
	 * 
	 * @param title the raw title, may be null
	 * @param rows amount of rows [1:9]
	 * @param player targetplayer, may be null
	 * @param previusHolder previusly used gui, may be null
	 * @param plugin the plugin responsible for this gui
	 */
	public ChestGui(String title, int rows,Player player, Gui previusHolder,@NotNull CorePlugin plugin) {
		this(title, rows, player, previusHolder, plugin, false);
	}
	
	/**
	 * Create a chesttype gui
	 * 
	 * @param title the raw title, may be null
	 * @param rows amount of rows [1:9]
	 * @param player targetplayer, may be null
	 * @param previusHolder previusly used gui, may be null
	 * @param plugin the plugin responsible for this gui
	 * @param isTimerUpdated update the inventory each seconds as long as it has at least a player
	 */
	public ChestGui(String title, int rows,Player player,Gui previusHolder,@NotNull CorePlugin plugin,boolean isTimerUpdated) {
		if (rows < 1 || rows >9)
			throw new IllegalArgumentException("invalid rows size '"+rows+"'");
		if (plugin == null)
			throw new NullPointerException();
		this.previusHolder = previusHolder;
		this.player = player;
		this.inv = Bukkit.createInventory(this, rows*9, UtilsString.fix(title,player,true));
		this.plugin = plugin;
		if (isTimerUpdated)
			GuiHandler.registerTimerUpdatedGui(this);
	}
	
	public CorePlugin getPlugin() {
		return plugin;
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@Override
	public final Player getTargetPlayer() {
		return player;
	}
	
	@Override
	public Gui getPreviusGui() {
		return previusHolder;
	}
	
	@Override
	public void onClose(InventoryCloseEvent event) {
		return;
	}
	
	/**
	 * Sets whenever the inventory should be updated when a player open it
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
	protected void giveMessageFeedback(CommandSender reicever, @NotNull String path,  String def, boolean color,  CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, getMessage(reicever, path, def, color, target, holders));
	}
	@Deprecated
	protected String getMessage(CommandSender reicever, @NotNull String path,  String def, boolean color,  CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui."+path,
				def,color,target, holders);
	}
	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @NotNull String path,  List<String> def, boolean color,  CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, getPlugin().getLanguageConfig(reicever).loadMessage("gui."+path,
				def,color,target, holders));
	}
	@Deprecated
	protected String getMessage(CommandSender reicever, @NotNull String path,  List<String> def, boolean color,  CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui."+path,
				def,color,target, holders);
	}
	
	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @NotNull String path,  String def, boolean color, String... holders) {
		giveMessageFeedback(reicever, path, def, color, reicever, holders);
	}@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @NotNull String path,  List<String> def, boolean color, String... holders) {
		giveMessageFeedback(reicever, path, def, color, reicever, holders);
	}@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @NotNull String path,  String def, String... holders) {
		giveMessageFeedback(reicever, path, def, true, reicever, holders);
	}@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @NotNull String path,  List<String> def, String... holders) {
		giveMessageFeedback(reicever, path, def, true, reicever, holders);
	}@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @NotNull String path,  String def,  CommandSender target, String... holders) {
		giveMessageFeedback(reicever, path, def, true, target, holders);
	}@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @NotNull String path,  List<String> def,  CommandSender target, String... holders) {
		giveMessageFeedback(reicever, path, def, true, target, holders);
	}
	@Deprecated
	protected String getMessage(CommandSender reicever, @NotNull String path,  String def, boolean color, String... holders) {
		return getMessage(reicever, path, def, color, reicever, holders);
	}@Deprecated
	protected String getMessage(CommandSender reicever, @NotNull String path,  List<String> def, boolean color, String... holders) {
		return getMessage(reicever, path, def, color, reicever, holders);
	}@Deprecated
	protected String getMessage(CommandSender reicever, @NotNull String path,  String def, String... holders) {
		return getMessage(reicever, path, def, true, reicever, holders);
	}@Deprecated
	protected String getMessage(CommandSender reicever, @NotNull String path,  List<String> def, String... holders) {
		return getMessage(reicever, path, def, true, reicever, holders);
	}@Deprecated
	protected String getMessage(CommandSender reicever, @NotNull String path,  String def,  CommandSender target, String... holders) {
		return getMessage(reicever, path, def, true, target, holders);
	}@Deprecated
	protected String getMessage(CommandSender reicever, @NotNull String path,  List<String> def,  CommandSender target, String... holders) {
		return getMessage(reicever, path, def, true, target, holders);
	}
	@Deprecated
	protected  ComponentBuilder getComponent(@NotNull CommandSender reicever, @NotNull String path,  String defMessage,  String defHover,  String defClick, ClickEvent.Action action, boolean color,  CommandSender target, String... holders){
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui."+path, defMessage, defHover, defClick, action, color, target, holders);
	}@Deprecated
	protected  ComponentBuilder getComponent(@NotNull CommandSender reicever, @NotNull String path,  String defMessage,  String defHover,  String defClick, ClickEvent.Action action, boolean color, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, color, reicever, holders);
	}@Deprecated
	protected  ComponentBuilder getComponent(@NotNull CommandSender reicever, @NotNull String path,  String defMessage,  String defHover,  String defClick, ClickEvent.Action action,  CommandSender target, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, true, target, holders);
	}@Deprecated
	protected  ComponentBuilder getComponent(@NotNull CommandSender reicever, @NotNull String path,  String defMessage,  String defHover,  String defClick, ClickEvent.Action action, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, true, reicever, holders);
	}@Deprecated
	protected  ComponentBuilder getComponent(@NotNull CommandSender reicever, @NotNull String path,  String defMessage,  String defHover,  String defClick, boolean color, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, color, reicever, holders);
	}@Deprecated
	protected  ComponentBuilder getComponent(@NotNull CommandSender reicever, @NotNull String path,  String defMessage,  String defHover,  String defClick,  CommandSender target, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, true, target, holders);
	}@Deprecated
	protected  ComponentBuilder getComponent(@NotNull CommandSender reicever, @NotNull String path,  String defMessage,  String defHover,  String defClick, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, true, reicever, holders);
	}@Deprecated
	protected  List<String> getMultiMessage(@NotNull CommandSender reicever ,@NotNull String path,  List<String> def, boolean color, String... holders){
		return getMultiMessage(reicever, path, def, color, reicever, holders);
	}@Deprecated
	protected  List<String> getMultiMessage(@NotNull CommandSender reicever, @NotNull String path,  List<String> def,  CommandSender target, String... holders){
		return getMultiMessage(reicever, path, def, true, target, holders);
	}@Deprecated
	protected  List<String> getMultiMessage(@NotNull CommandSender reicever, @NotNull String path,  List<String> def, String... holders){
		return getMultiMessage(reicever, path, def, true, reicever, holders);
	}@Deprecated
	protected  List<String> getMultiMessage(@NotNull CommandSender reicever, @NotNull String path,  List<String> def, boolean color,  CommandSender target, String... holders){
		return getPlugin().getLanguageConfig(reicever).loadMultiMessage("gui."+path, def, color, reicever, holders);
	}

}
