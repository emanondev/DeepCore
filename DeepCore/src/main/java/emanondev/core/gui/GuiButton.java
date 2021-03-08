package emanondev.core.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import emanondev.core.CorePlugin;

public interface GuiButton {

	/**
	 * This event is called when the player drags an item in their cursor across the
	 * inventory. The ItemStack is distributed across the slots the HumanEntity
	 * dragged over. The method of distribution is described by the DragType
	 * returned by <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/InventoryDragEvent.html#getType--"><code>getType()</code></a>.
	 * <p>
	 * Canceling this event will result in none of the changes described in <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/InventoryDragEvent.html#getNewItems--"><code>getNewItems()</code></a>
	 * being applied to the Inventory.
	 * <p>
	 * Because InventoryDragEvent occurs within a modification of the Inventory, not
	 * all Inventory related methods are safe to use.
	 * <p>
	 * The following should never be invoked by an EventHandler for
	 * InventoryDragEvent using the HumanEntity or InventoryView associated with
	 * this event.
	 * <ul>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#closeInventory--"><code>HumanEntity.closeInventory()</code></a>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openInventory-org.bukkit.inventory.Inventory-"><code>HumanEntity.openInventory(Inventory)</code></a>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openWorkbench-org.bukkit.Location-boolean-"><code>HumanEntity.openWorkbench(Location, boolean)</code></a>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openEnchanting-org.bukkit.Location-boolean-"><code>HumanEntity.openEnchanting(Location, boolean)</code></a>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/InventoryView.html#close--"><code>InventoryView.close()</code></a>
	 * </ul>
	 * To invoke one of these methods, schedule a task using <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html#runTask-org.bukkit.plugin.Plugin-java.lang.Runnable-"><code>BukkitScheduler.runTask(Plugin, Runnable)</code></a>,
	 * which will run the task on the next tick. Also be aware that this is not an
	 * exhaustive list, and other methods could potentially create issues as well.
	 * <p>
	 * Assuming the EntityHuman associated with this event is an instance of a
	 * Player, manipulating the MaxStackSize or contents of an Inventory will
	 * require an Invocation of <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#updateInventory--"><code>Player.updateInventory()</code></a>.
	 * <p>
	 * Any modifications to slots that are modified by the results of this
	 * InventoryDragEvent will be overwritten. To change these slots, this event
	 * should be cancelled and the changes applied. Alternatively, scheduling a task
	 * using <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html#runTask-org.bukkit.plugin.Plugin-java.lang.Runnable-"><code>BukkitScheduler.runTask(Plugin, Runnable)</code></a>,
	 * which would execute the task on the next tick, would work as well.
	 * 
	 * @param event - the event
	 * @return true if should call getGui().inventoryUpdate();
	 */
	public boolean onDrag(InventoryDragEvent event);

	/**
	 * This event is called when a player clicks a slot in an inventory.
	 * <p>
	 * Because InventoryClickEvent occurs within a modification of the Inventory,
	 * not all Inventory related methods are safe to use.
	 * <p>
	 * The following should never be invoked by an EventHandler for
	 * InventoryClickEvent using the HumanEntity or InventoryView associated with
	 * this event:
	 * <ul>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#closeInventory--"><code>HumanEntity.closeInventory()</code></a>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openInventory-org.bukkit.inventory.Inventory-"><code>HumanEntity.openInventory(Inventory)</code></a>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openWorkbench-org.bukkit.Location-boolean-"><code>HumanEntity.openWorkbench(Location, boolean)</code></a>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openEnchanting-org.bukkit.Location-boolean-"><code>HumanEntity.openEnchanting(Location, boolean)</code></a>
	 * <li><a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/InventoryView.html#close--"><code>InventoryView.close()</code></a>
	 * </ul>
	 * To invoke one of these methods, schedule a task using <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html#runTask-org.bukkit.plugin.Plugin-java.lang.Runnable-"><code>BukkitScheduler.runTask(Plugin, Runnable)</code></a>,
	 * which will run the task on the next tick. Also be aware that this is not an
	 * exhaustive list, and other methods could potentially create issues as well.
	 * <p>
	 * Assuming the EntityHuman associated with this event is an instance of a
	 * Player, manipulating the MaxStackSize or contents of an Inventory will
	 * require an Invocation of <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#updateInventory--"><code>Player.updateInventory()</code></a>.
	 * <p>
	 * Modifications to slots that are modified by the results of this
	 * InventoryClickEvent can be overwritten. To change these slots, this event
	 * should be cancelled and all desired changes to the inventory applied.
	 * Alternatively, scheduling a task using <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html#runTask-org.bukkit.plugin.Plugin-java.lang.Runnable-"><code>BukkitScheduler.runTask(
	Plugin, Runnable)</code></a>, which would execute the task on the next tick,
	 * would work as well.
	 * 
	 * @param event - the event
	 * @return true if should call getGui().inventoryUpdate();
	 */
	public boolean onClick(InventoryClickEvent event);
	
	public ItemStack getItem();
	
	public default CorePlugin getPlugin() {
		return getGui().getPlugin();
	}
	
	public Gui getGui();
	
	public default Player getTargetPlayer() {
		return getGui().getTargetPlayer();
	}

}
