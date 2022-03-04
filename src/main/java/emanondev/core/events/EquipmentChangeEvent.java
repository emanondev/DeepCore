package emanondev.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentChangeEvent extends PlayerEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }


    private final ItemStack from;
    private final ItemStack to;
    private final EquipmentSlot slot;
    private final EquipMethod method;


    public EquipmentChangeEvent(Player who, EquipMethod method, EquipmentSlot slot, ItemStack from, ItemStack to) {
        super(who);
        this.from = from;
        this.to = to;
        this.slot = slot;
        this.method = method;
    }


    public ItemStack getFrom() {
        return from;
    }


    public ItemStack getTo() {
        return to;
    }


    public EquipmentSlot getSlotType() {
        return slot;
    }


    public EquipMethod getMethod() {
        return method;
    }


    public enum EquipMethod {// These have got to be the worst documentations ever.
        /**
         * When you shift click an armor piece to equip or unequip
         */
        SHIFT_CLICK,
        /**
         * When you drag and drop the item to equip or unequip
         */
        INVENTORY_DRAG,
        /**
         * When in range of a dispenser that shoots an armor piece to equip.<br>
         * Requires the spigot version to have
         * {@link org.bukkit.event.block.BlockDispenseArmorEvent} implemented. Which is
         * 1.13.1+.
         */
        DISPENSER,
        /**
         * When an armor piece is removed due to it losing all durability.
         */
        BROKE,
        /**
         * When you die causing all armor to unequip
         */
        DEATH, PLUGIN_WORLD_CHANGE, COMMAND, RIGHT_CLICK, SWAP_HANDS_ITEM, RESPAWN, HOTBAR_HAND_CHANGE, PICKUP, CONSUME, DROP,
        SHEEP_COLOR, NAMETAG_APPLY, UNKNOWN, INVENTORY_DROP, INVENTORY_PICKUP, INVENTORY_PLACE, INVENTORY_HOTBAR_SWAP,
        INVENTORY_MOVE_TO_OTHER_INVENTORY, INVENTORY_COLLECT_TO_CURSOR, INVENTORY_SWAP_WITH_CURSOR, USE,
        ARMOR_STAND_MANIPULATE, USE_ON_ENTITY
    }

}
