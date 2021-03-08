package emanondev.core.gui;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.permissions.Permission;

import emanondev.core.UtilsMessages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class AGuiButton implements GuiButton {
	private Gui parent;
	
	public AGuiButton(Gui parent) {
		this.parent = parent;
	}
	public Gui getGui() {
		return parent;
	}
	

	/**
	 * Notify sender the lack of permission as specified at
	 * 'generic.lack_permission' path of language file
	 * 
	 * @param target Notify target 
	 * @param perm Permission to test
	 */
	protected void permissionLackNotify(CommandSender target, Permission perm) {
		getPlugin().getLanguageConfig(target).loadMessage("generic.lack_permission",
				"&cYou lack of permission %permission%", "%permission%", perm.getName());
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, loadMessage(reicever, path, def, color, target, holders));
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color,
			@Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui_button." + path, def, color,
				target, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, loadMessage(reicever, path, def, color, target, holders));
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui_button." + path, def, color,
				target, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			boolean color, String... holders) {
		sendMessageFeedback(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, String... holders) {
		sendMessageFeedback(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			String... holders) {
		sendMessageFeedback(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			String... holders) {
		sendMessageFeedback(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			@Nullable CommandSender target, String... holders) {
		sendMessageFeedback(reicever, path, def, true, target, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 */
	protected void sendMultiMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			@Nullable CommandSender target, String... holders) {
		sendMessageFeedback(reicever, path, def, true, target, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color,
			String... holders) {
		return loadMessage(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, String... holders) {
		return loadMessage(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable String def,
			String... holders) {
		return loadMessage(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			String... holders) {
		return loadMessage(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable String def,
			@Nullable CommandSender target, String... holders) {
		return loadMessage(reicever, path, def, true, target, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			@Nullable CommandSender target, String... holders) {
		return loadMessage(reicever, path, def, true, target, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadComponentMessage("gui_button." + path,
				defMessage, defHover, defClick, action, color, target, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			boolean color, String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, action, color, reicever, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			@Nullable CommandSender target, String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, action, true, target, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, action, true, reicever, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, boolean color,
			String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, null, color, reicever, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick,
			@Nullable CommandSender target, String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, null, true, target, holders);
	}

	/**
	 * Get message and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, null, true, reicever, holders);
	}

	/**
	 * Get message list and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable List<String> loadMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, boolean color, String... holders) {
		return loadMultiMessage(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Get message list and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable List<String> loadMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, @Nullable CommandSender target, String... holders) {
		return loadMultiMessage(reicever, path, def, true, target, holders);
	}

	/**
	 * Get message list and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable List<String> loadMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, String... holders) {
		return loadMultiMessage(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Get message list and set default if absent
	 * 
	 * @param reicever Message reicever
	 * @param path
	 *            final configuration path is
	 *            <b>'gui_button.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable List<String> loadMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMultiMessage("gui_button." + path, def,
				color, reicever, holders);
	}
	
	

	@Deprecated protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, getMessage(reicever, path, def, color, target, holders));
	}
	@Deprecated protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui_button."+path,
				def,color,target, holders);
	}
	@Deprecated protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, getPlugin().getLanguageConfig(reicever).loadMessage("gui_button."+path,
				def,color,target, holders));
	}
	@Deprecated protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("gui_button."+path,
				def,color,target, holders);
	}
	

	@Deprecated protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color, String... holders) {
		giveMessageFeedback(reicever, path, def, color, reicever, holders);
	}
	@Deprecated protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, String... holders) {
		giveMessageFeedback(reicever, path, def, color, reicever, holders);
	}
	@Deprecated protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def, String... holders) {
		giveMessageFeedback(reicever, path, def, true, reicever, holders);
	}
	@Deprecated protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, String... holders) {
		giveMessageFeedback(reicever, path, def, true, reicever, holders);
	}
	@Deprecated protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def, @Nullable CommandSender target, String... holders) {
		giveMessageFeedback(reicever, path, def, true, target, holders);
	}
	@Deprecated protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, @Nullable CommandSender target, String... holders) {
		giveMessageFeedback(reicever, path, def, true, target, holders);
	}

	@Deprecated protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color, String... holders) {
		return getMessage(reicever, path, def, color, reicever, holders);
	}
	@Deprecated protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, String... holders) {
		return getMessage(reicever, path, def, color, reicever, holders);
	}
	@Deprecated protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, String... holders) {
		return getMessage(reicever, path, def, true, reicever, holders);
	}
	@Deprecated protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, String... holders) {
		return getMessage(reicever, path, def, true, reicever, holders);
	}
	@Deprecated protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, @Nullable CommandSender target, String... holders) {
		return getMessage(reicever, path, def, true, target, holders);
	}
	@Deprecated protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, @Nullable CommandSender target, String... holders) {
		return getMessage(reicever, path, def, true, target, holders);
	}
	
	@Deprecated protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, boolean color, @Nullable CommandSender target, String... holders){
		return getPlugin().getLanguageConfig(reicever).loadComponentMessage("gui_button."+path, defMessage, defHover, defClick, action, color, target, holders);
	}
	@Deprecated protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, boolean color, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, color, reicever, holders);
	}
	@Deprecated protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, @Nullable CommandSender target, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, true, target, holders);
	}
	@Deprecated protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, action, true, reicever, holders);
	}
	@Deprecated protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, boolean color, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, color, reicever, holders);
	}
	@Deprecated protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, @Nullable CommandSender target, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, true, target, holders);
	}
	@Deprecated protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, String... holders){
		return getComponent(reicever, path, defMessage, defHover, defClick, null, true, reicever, holders);
	}
	@Deprecated protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever ,@Nonnull String path, @Nullable List<String> def, boolean color, String... holders){
		return getMultiMessage(reicever, path, def, color, reicever, holders);
	}
	@Deprecated protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable List<String> def, @Nullable CommandSender target, String... holders){
		return getMultiMessage(reicever, path, def, true, target, holders);
	}
	@Deprecated protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable List<String> def, String... holders){
		return getMultiMessage(reicever, path, def, true, reicever, holders);
	}
	@Deprecated protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders){
		return getPlugin().getLanguageConfig(reicever).loadMultiMessage("gui_button."+path, def, color, reicever, holders);
	}

	public boolean onDrag(InventoryDragEvent event) {
		return false;
	}

}