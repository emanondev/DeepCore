package emanondev.core.gui;

import emanondev.core.CorePlugin;
import emanondev.core.YMLSection;
import emanondev.core.util.FileLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Gui extends InventoryHolder, FileLogger {


    /**
     * Returns language section for the command sender<br>
     * (load language config and go to sub pattern 'guis')
     *
     * @param who may be null
     * @return language section for the command sender
     * @see emanondev.core.message.DMessage
     */
    @Deprecated
    @NotNull
    default YMLSection getLanguageSection(@Nullable CommandSender who) {
        return getPlugin().getLanguageConfig(who).loadSection("guis");
    }

    /**
     * @return plugin responsible for the gui, cannot be null
     */
    @NotNull CorePlugin getPlugin();

    default void open(@NotNull HumanEntity p) {
        p.openInventory(getInventory());
    }

    /**
     * @return inventory of this holder, cannot be null
     */
    @NotNull Inventory getInventory();

    /**
     * @return parent Gui or null
     * @see #getPreviousGui()
     */
    @Deprecated
    @Nullable
    default Gui getPreviusGui() {
        return getPreviousGui();
    }

    /**
     * @return parent Gui or null
     */
    @Nullable Gui getPreviousGui();

    void onClose(@NotNull InventoryCloseEvent event);

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
     * <p>
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
     * @return true if the inventory is updated when a player open it
     */
    boolean isUpdateOnOpen();

    /**
     * Sets whenever the inventory should be updated when a player open it
     *
     * @param value whenever the inventory should be updated when a player open it or not
     */
    void setUpdateOnOpen(boolean value);

    /**
     * Update the slots of the inventory
     */
    void updateInventory();

    /**
     * @param slot - slot position
     * @return button at slot position or null
     * @throws UnsupportedOperationException if the get operation is not supported by this gui
     */
    @Nullable GuiButton getButton(int slot);

    /**
     * @param slot
     * @param button
     * @throws UnsupportedOperationException if the set operation is not supported by this gui
     */
    void setButton(int slot, @Nullable GuiButton button);

    /**
     * @param button
     * @throws UnsupportedOperationException if the add operation is not supported by this gui
     */
    void addButton(@NotNull GuiButton button);

    /**
     * @throws UnsupportedOperationException if the add operation is not supported by this gui
     */
    void clearButtons();

    /**
     * @return target player or null
     */
    @Nullable Player getTargetPlayer();

    default boolean isTimerUpdated() {
        return false;
    }

    default void setTimerUpdated(boolean value) {
        new UnsupportedOperationException("This gui doesn't support timer updated option").printStackTrace();
    }

    default void onTimerUpdate() {
    }

}
