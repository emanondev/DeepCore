package emanondev.core.gui.anvil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import emanondev.core.CorePlugin;
import emanondev.core.gui.Gui;
import emanondev.core.gui.GuiButton;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * An anvil gui, used for gathering a user's input
 *
 * @author Wesley Smith
 * @since 1.0
 */
public class AnvilInternalGui implements Gui {

	/**
	 * The local {@link VersionWrapper} object for the server's version
	 */
	private static VersionWrapper WRAPPER = new Wrapper1_16_R2();

	/**
	 * The {@link Plugin} that this anvil GUI is associated with
	 */
	private final CorePlugin plugin;
	/**
	 * The player who has the GUI open
	 */
	private final Player player;
	/**
	 * The title of the anvil inventory
	 */
	private String inventoryTitle;
	/**
	 * The ItemStack that is in the {@link Slot#INPUT_LEFT} slot.
	 */
	private ItemStack insert;
	/**
	 * A state that decides where the anvil GUI is able to be closed by the user
	 */
	private final boolean preventClose;
	/**
	 * An {@link Consumer} that is called when the anvil GUI is closed
	 */
	// private final Consumer<Player> closeListener;
	/**
	 * An {@link BiFunction} that is called when the {@link Slot#OUTPUT} slot has
	 * been clicked
	 */
	// private final BiFunction<Player, String, Response> completeFunction;

	/**
	 * The container id of the inventory, used for NMS methods
	 */
	private int containerId;
	/**
	 * The inventory that is used on the Bukkit side of things
	 */
	private Inventory inventory;
	/**
	 * The listener holder class
	 */
	private final ListenUp listener = new ListenUp();

	/**
	 * Represents the state of the inventory being open
	 */
	private boolean open;

	private Consumer<InventoryClickEvent> onClick;

	/**
	 * Create an AnvilGUI and open it for the player.
	 *
	 * @param plugin         A {@link CorePlugin} instance
	 * @param player         The {@link Player} to open the inventory for
	 * @param inventoryTitle What to have the text already set to
	 * @param itemText       The name of the item in the first slot of the anvilGui
	 * @param insert         The material of the item in the first slot of the
	 *                       anvilGUI
	 * @param onClick A {@link Consumer} handle the click
	 * @param preventClose   Whether to prevent the inventory from closing
	 */
	// * @param closeListener A {@link Consumer} when the inventory closes
	// * @param completeFunction A {@link BiFunction} that is called when the player
	// clicks the {@link Slot#OUTPUT} slot

	public AnvilInternalGui(CorePlugin plugin, Player player, String inventoryTitle, String itemText, ItemStack insert,
			boolean preventClose, Consumer<InventoryClickEvent> onClick// ,
	// Consumer<Player> closeListener,
	// BiFunction<Player, String, Response> completeFunction
	) {
		this.plugin = plugin;
		this.player = player;
		this.inventoryTitle = inventoryTitle;
		this.insert = insert;
		this.preventClose = preventClose;
		// this.closeListener = closeListener;
		// this.completeFunction = completeFunction;

		if (itemText != null) {
			if (insert == null) {
				this.insert = new ItemStack(Material.PAPER);
			}

			ItemMeta paperMeta = this.insert.getItemMeta();
			paperMeta.setDisplayName(itemText);
			this.insert.setItemMeta(paperMeta);
		}
		openInventory();
	}

	/**
	 * Opens the anvil GUI
	 */
	private void openInventory() {
		WRAPPER.handleInventoryCloseEvent(player);
		WRAPPER.setActiveContainerDefault(player);

		Bukkit.getPluginManager().registerEvents(listener, plugin);

		final Object container = WRAPPER.newContainerAnvil(player, inventoryTitle);

		inventory = WRAPPER.toBukkitInventory(container);
		inventory.setItem(0, this.insert);

		containerId = WRAPPER.getNextContainerId(player, container);
		WRAPPER.sendPacketOpenWindow(player, containerId, inventoryTitle);
		WRAPPER.setActiveContainer(player, container);
		WRAPPER.setActiveContainerId(container, containerId);
		WRAPPER.addActiveContainerSlotListener(container, player);
		open = true;
	}

	/**
	 * Closes the inventory if it's open.
	 */
	public void closeInventory() {
		if (!open) {
			return;
		}

		open = false;

		WRAPPER.handleInventoryCloseEvent(player);
		WRAPPER.setActiveContainerDefault(player);
		WRAPPER.sendPacketCloseWindow(player, containerId);

		HandlerList.unregisterAll(listener);

		// if(closeListener != null) {
		// closeListener.accept(player);
		// }
	}

	/**
	 * Returns the Bukkit inventory for this anvil gui
	 * 
	 * @return the {@link Inventory} for this anvil gui
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/*
	 * /** Simply holds the listeners for the GUI
	 */
	private class ListenUp implements Listener {

		@EventHandler
		public void onInventoryClick(InventoryClickEvent event) {
			if (!(event.getInventory().equals(inventory)))
				return;
			event.setCancelled(true);
			if (event.getClickedInventory() != null
					&& event.getClickedInventory().equals(event.getView().getTopInventory())) {
				// Gui holder = (Gui) event.getView().getTopInventory().getHolder();
				if (event.getWhoClicked() instanceof Player)
					AnvilInternalGui.this.onClick(event);
			}
		}

		@EventHandler
		public void onInventoryDrag(InventoryDragEvent event) {
			if (event.getInventory().equals(inventory))
				event.setCancelled(true);
			/*
			 * { for (int slot : Slot.values()) { if (event.getRawSlots().contains(slot)) {
			 * event.setCancelled(true); break; } } }
			 */
		}

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent event) {
			if (open && event.getInventory().equals(inventory)) {
				closeInventory();
				if (preventClose) {
					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						public void run() {
							AnvilInternalGui.this.openInventory();
						}
					});
				}
			}
		}
	}

	@Override
	public Gui getPreviusGui() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	@Override
	public void onClick(InventoryClickEvent event) {
		onClick.accept(event);
	}

	@Override
	public void onDrag(InventoryDragEvent event) {
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
	}

	@Override
	public GuiButton getButton(int slot) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setButton(int slot, GuiButton button) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addButton(GuiButton button) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateInventory() {
	}

	@Override
	public CorePlugin getPlugin() {
		return plugin;
	}

	@Override
	public Player getTargetPlayer() {
		return player;
	}

}