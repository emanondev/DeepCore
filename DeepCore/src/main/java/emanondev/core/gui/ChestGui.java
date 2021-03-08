package emanondev.core.gui;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import emanondev.core.CorePlugin;
import emanondev.core.UtilsMessages;
import emanondev.core.UtilsString;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;


public abstract class ChestGui implements Gui {

	private final Gui previusHolder;
	private Player player;
	private Inventory inv;
	private CorePlugin plugin;

	/**
	 * Create a chesttype gui
	 * 
	 * @param title - the raw title
	 * @param rows - amount of rows [1:9]
	 * @param p - targetplayer <br> might be null
	 * @param previusHolder - previusly used gui <br> might be null
	 * @param plugin - the plugin responsible for this gui
	 */
	public ChestGui(@Nullable String title, int rows,@Nullable Player p,@Nullable Gui previusHolder,@Nonnull CorePlugin plugin) {
		if (rows < 1 || rows >9)
			throw new IllegalArgumentException("invalid rows size '"+rows+"'");
		if (plugin == null)
			throw new NullPointerException();
		this.previusHolder = previusHolder;
		this.inv = Bukkit.createInventory(this, rows*9, UtilsString.fix(title,player,true));
		this.player = p;
		this.plugin = plugin;
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
/*
	@Override
	public void onTrade(TradeSelectEvent event) {
		return;
	}*/

	@Override
	public void onOpen(InventoryOpenEvent event) {
		return;
	}

	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, getMessage(reicever, path, def, color, target, holders));
	}
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui."+path,
				def,color,target, holders);
	}
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, getPlugin().getLanguageConfig(reicever).loadMessage("gui."+path,
				def,color,target, holders));
	}
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui."+path,
				def,color,target, holders);
	}
	

	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color, String... holders) {
		giveMessageFeedback(reicever, path, def, color, reicever, holders);
	}
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, String... holders) {
		giveMessageFeedback(reicever, path, def, color, reicever, holders);
	}
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def, String... holders) {
		giveMessageFeedback(reicever, path, def, true, reicever, holders);
	}
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, String... holders) {
		giveMessageFeedback(reicever, path, def, true, reicever, holders);
	}
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def, @Nullable CommandSender target, String... holders) {
		giveMessageFeedback(reicever, path, def, true, target, holders);
	}
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, @Nullable CommandSender target, String... holders) {
		giveMessageFeedback(reicever, path, def, true, target, holders);
	}

	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color, String... holders) {
		return getMessage(reicever, path, def, color, reicever, holders);
	}
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, String... holders) {
		return getMessage(reicever, path, def, color, reicever, holders);
	}
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, String... holders) {
		return getMessage(reicever, path, def, true, reicever, holders);
	}
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, String... holders) {
		return getMessage(reicever, path, def, true, reicever, holders);
	}
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, @Nullable CommandSender target, String... holders) {
		return getMessage(reicever, path, def, true, target, holders);
	}
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, @Nullable CommandSender target, String... holders) {
		return getMessage(reicever, path, def, true, target, holders);
	}
	
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, boolean color, @Nullable CommandSender target, String... holders){
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui."+path, defMessage, defHover, defClick, action, color, target, holders);
	}
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, boolean color, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, color, reicever, holders);
	}
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, @Nullable CommandSender target, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, true, target, holders);
	}
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, true, reicever, holders);
	}
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, boolean color, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, color, reicever, holders);
	}
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, @Nullable CommandSender target, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, true, target, holders);
	}
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, true, reicever, holders);
	}
	protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever ,@Nonnull String path, @Nullable List<String> def, boolean color, String... holders){
		return getMultiMessage(reicever, path, def, color, reicever, holders);
	}
	protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable List<String> def, @Nullable CommandSender target, String... holders){
		return getMultiMessage(reicever, path, def, true, target, holders);
	}
	protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable List<String> def, String... holders){
		return getMultiMessage(reicever, path, def, true, reicever, holders);
	}
	protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders){
		return getPlugin().getLanguageConfig(reicever).loadMultiMessage("gui."+path, def, color, reicever, holders);
	}

}
