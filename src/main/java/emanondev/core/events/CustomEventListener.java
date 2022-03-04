package emanondev.core.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import emanondev.core.CoreMain;
import net.md_5.bungee.api.ChatColor;

public class CustomEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private static void eventR(InventoryClickEvent event) {

        if (event.getSlotType() == SlotType.RESULT && event.getCurrentItem() != null
                && !event.getCurrentItem().getType().isAir()) {
            switch (event.getView().getTopInventory().getType()) {
                case GRINDSTONE:
                case ANVIL:
                case MERCHANT:
                case STONECUTTER:
                case CARTOGRAPHY:
                    checkResult(event);
                default:
                    break;
            }
        }
        /*
         * else if (event.getSlotType()==SlotType.CRAFTING ) { switch
         * (event.getView().getTopInventory().getType()) { case GRINDSTONE: case ANVIL:
         * case MERCHANT: case STONECUTTER: case CARTOGRAPHY: checkIngredientAdd(event);
         * } }
         */
    }

    private static void checkResult(InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT:
            case RIGHT: {
                if (event.getCursor() != null && !event.getCursor().getType().isAir()) {
                    if (!event.getCurrentItem().isSimilar(event.getCursor()))
                        return;
                    if (event.getCursor().getMaxStackSize() - event.getCursor().getAmount() < event.getCurrentItem()
                            .getAmount())
                        return;
                }
                craftResultEvent(event);
                return;
            }
            case SHIFT_LEFT:
            case SHIFT_RIGHT: {
                if (event.getView().getBottomInventory().firstEmpty() == -1) {
                    for (ItemStack item : event.getView().getBottomInventory().getStorageContents()) {
                        if (event.getCurrentItem().isSimilar(item) && item.getAmount() != item.getMaxStackSize()) {
                            craftResultEvent(event);
                            return;
                        }
                    }
                } else
                    craftResultEvent(event);
                return;
            }
            case NUMBER_KEY: {
                ItemStack barItem = event.getView().getBottomInventory().getItem(event.getHotbarButton());
                if (barItem != null && !barItem.getType().isAir())
                    return;
                craftResultEvent(event);
                return;
            }
            case DROP: {
                if (event.getCursor() != null && !event.getCursor().getType().isAir())
                    return;
                // if (event.getCurrentItem()==null || event.getCursor().getType().isAir())
                // return;
                craftResultEvent(event);
                return;
            }
            default:
                return;
        }
    }

    private static void craftResultEvent(InventoryClickEvent event) {
        switch (event.getView().getTopInventory().getType()) {
            case ANVIL:
                Bukkit.getPluginManager().callEvent(new AnvilCraftEvent(event));
                return;
            case CARTOGRAPHY:
                Bukkit.getPluginManager().callEvent(new CartographyCraftEvent(event));
                return;
            case GRINDSTONE:
                Bukkit.getPluginManager().callEvent(new GrindstoneCraftEvent(event));
                return;
            case MERCHANT:
                Bukkit.getPluginManager().callEvent(new MerchantCraftEvent(event));
                return;
            case STONECUTTER:
                Bukkit.getPluginManager().callEvent(new StonecutterCraftEvent(event));
                return;
            default:
                break;

        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private static void event(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            CoreMain.get().logTetraStar(ChatColor.YELLOW, "Someone not a Player clicked Inventory (!)");
            return;
        }
        merchantCheck(event);
        if (event.isCancelled())
            return;
        anvilCheck(event);
    }

    private static void anvilCheck(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof AnvilInventory))
            return;
        if (event.getSlot() != 2)
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;
        Bukkit.getPluginManager().callEvent(new AnvilResultCreationEvent(event));
    }

    private static void merchantCheck(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof MerchantInventory))
            return;
        if (event.getSlot() != 2)
            return;
        if (event.getClickedInventory().getItem(2) == null
                || event.getClickedInventory().getItem(2).getType() == Material.AIR)
            return;
        Bukkit.getPluginManager().callEvent(new PlayerBuyMerchantRecipeEvent(event));
    }

}
