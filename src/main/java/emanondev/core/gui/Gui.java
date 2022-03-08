package emanondev.core.gui;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import emanondev.core.CorePlugin;
import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Gui extends InventoryHolder {
	

	/**
	 * Returns language section for the command sender<br>
	 * (load language config and go to sub pattern 'command.[command id]')
	 * @param who may be null
	 * @return language section for the command sender
	 */
	@NotNull default YMLSection getLanguageSection(CommandSender who) {
		return getPlugin().getLanguageConfig(who).loadSection("guis");
	}

	default void open(HumanEntity p) {
		p.openInventory(getInventory());
	}

	/**
	 * 
	 * @return parent Gui or null
	 */
	@Deprecated
	@Nullable default Gui getPreviusGui(){
		return getPreviousGui();
	}

	/**
	 *
	 * @return parent Gui or null
	 */
	@Nullable Gui getPreviousGui();

	void onClose(InventoryCloseEvent event);

	/**
	 * This event is called when a player clicks in an inventory.
	 * <p>
	 * The event is cancelled by default for gui.
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
	 * 
	 * Modifications to slots that are modified by the results of this
	 * InventoryClickEvent can be overwritten. To change these slots, this event
	 * should be cancelled and all desired changes to the inventory applied.
	 * Alternatively, scheduling a task using <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html#runTask-org.bukkit.plugin.Plugin-java.lang.Runnable-"><code>BukkitScheduler.runTask(Plugin, Runnable)</code></a>,
	 * which would execute the task on the next tick, would work as well.
	 */
	void onClick(@NotNull InventoryClickEvent event);

	/**
	 * This event is called when the player drags an item in their cursor across the
	 * inventory. The ItemStack is distributed across the slots the HumanEntity
	 * dragged over. The method of distribution is described by the DragType
	 * returned by <a href=
	 * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/InventoryDragEvent.html#getType--"><code>getType()</code></a>.
	 * <p>
	 * The event is cancelled by default for gui.
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
	 */
	default void onDrag(@NotNull InventoryDragEvent event) {
	}

	/**
	 * @param event - the event
	 */
	default void onOpen(@NotNull InventoryOpenEvent event) {
		if (isUpdateOnOpen())
			this.updateInventory();
	}

	/**
	 * @param slot - slot position
	 * @return button at slot position or null
	 */
	@Nullable GuiButton getButton(int slot);

	void setButton(int slot, @Nullable GuiButton button);

	void addButton(@NotNull GuiButton button);

	/**
	 * Update the slots of the inventory
	 */
	void updateInventory();

	/**
	 * @return inventory of this holder, cannot be null
	 */
	@NotNull Inventory getInventory();

	/**
	 * @return plugin responsible for the gui, cannot be null
	 */
	@NotNull CorePlugin getPlugin();

	/**
	 * 
	 * @return target player or null
	 */
	@Nullable Player getTargetPlayer();

	/**
	 * Sets whenever the inventory should be updated when a player open it
	 * 
	 * @param value whenever the inventory should be updated when a player open it or not
	 */
	void setUpdateOnOpen(boolean value);

	/**
	 * @return true if the inventory is updated when a player open it
	 */
	boolean isUpdateOnOpen();
	
}
