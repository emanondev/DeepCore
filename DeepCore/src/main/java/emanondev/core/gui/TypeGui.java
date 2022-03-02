package emanondev.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import emanondev.core.CorePlugin;
import emanondev.core.UtilsString;

public class TypeGui implements Gui {

	private final Gui previusHolder;
	private final Player player;
	private final Inventory inv;
	private final InventoryType type;
	private final CorePlugin plugin;
	private boolean updateOnOpen = true;
	private final GuiButton[] buttons;

	/**
	 * Create a chesttype gui
	 * 
	 * @param title         the raw title
	 * @param p             targetplayer
	 * @param previusHolder previusly used gui
	 * @param plugin        the plugin responsible for this gui
	 */
	public TypeGui(@Nullable String title, @NotNull InventoryType type, @Nullable Player p,
			@Nullable Gui previusHolder, @NotNull CorePlugin plugin) {
		this(title, type, p, previusHolder, plugin, false);
	}

	/**
	 * Create a gui
	 * 
	 * @param title          the raw title
	 * @param type           inventorytype
	 * @param p              targetplayer
	 * @param previusHolder  previusly used gui
	 * @param plugin         the plugin responsible for this gui
	 * @param isTimerUpdated update the inventory each seconds as long as it has at
	 *                       least a player
	 */
	public TypeGui(@Nullable String title, @NotNull InventoryType type, @Nullable Player p,
			@Nullable Gui previusHolder, @NotNull CorePlugin plugin, boolean isTimerUpdated) {
		if (plugin == null || type == null)
			throw new NullPointerException();
		this.previusHolder = previusHolder;
		this.player = p;
		this.type = type;
		this.inv = Bukkit.createInventory(this, type, UtilsString.fix(title, player, true));
		this.plugin = plugin;
		this.buttons = new GuiButton[inv.getSize()];
		if (isTimerUpdated)
			GuiHandler.registerTimerUpdatedGui(this);
	}

	public InventoryType getInventoryType() {
		return type;
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

	@Override
	public void onClick(InventoryClickEvent event) {
		if (event.getRawSlot() >= 0 && event.getRawSlot() < getInventory().getSize())
			if (buttons[event.getRawSlot()] != null && buttons[event.getRawSlot()].onClick(event))
				updateInventory();
	}

	@Override
	public GuiButton getButton(int slot) {
		return buttons[slot];
	}

	@Override
	public void setButton(int slot, GuiButton button) {
		buttons[slot] = button;
	}

	@Override
	public void addButton(GuiButton button) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateInventory() {
		for (int i = 0; i < inv.getSize(); i++)
			if (buttons[i] != null)
				inv.setItem(i, buttons[i].getItem());
	}

}
