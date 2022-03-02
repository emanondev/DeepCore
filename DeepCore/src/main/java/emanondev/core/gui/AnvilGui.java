package emanondev.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import emanondev.core.CorePlugin;
import emanondev.core.UtilsString;

public abstract class AnvilGui implements TextGui {

	private final Gui previusHolder;
	private final Player player;
	private final Inventory inv;
	private final CorePlugin plugin;
	private boolean updateOnOpen = true;
	
	/**
	 * Create a chesttype gui
	 * 
	 * @param title the raw title
	 * @param rows amount of rows [1:9]
	 * @param p targetplayer
	 * @param previusHolder previusly used gui
	 * @param plugin the plugin responsible for this gui
	 */
	public AnvilGui(@Nullable String title, @Nullable Player p,@Nullable Gui previusHolder,@NotNull CorePlugin plugin) {
		this(title,  p, previusHolder, plugin, false);
	}
	
	/**
	 * Create a chesttype gui
	 * 
	 * @param title the raw title
	 * @param rows amount of rows [1:9]
	 * @param p targetplayer
	 * @param previusHolder previusly used gui
	 * @param plugin the plugin responsible for this gui
	 * @param isTimerUpdated update the inventory each seconds as long as it has at least a player
	 */
	public AnvilGui(@Nullable String title, @Nullable Player p,@Nullable Gui previusHolder,@NotNull CorePlugin plugin,boolean isTimerUpdated) {
		if (plugin == null)
			throw new NullPointerException();
		this.previusHolder = previusHolder;
		this.player = p;
		this.inv = Bukkit.createInventory(this, InventoryType.ANVIL, UtilsString.fix(title,player,true));
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


}
