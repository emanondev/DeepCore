package emanondev.core.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffectType;

/**
 * USE MerchantCraftEvent
 * <p>
 * This event is called each time a player try to buy something from a merchant.<br><br>
 * <p>
 * Event is also called if player buy none because inventory is too full or has not enough ingredients.<br>
 * Cancelling this event prevent player from buyng stuffs but also glich clientside,<br>
 * might show as locked some trades after cancelling them,<br>
 * best solution to avoid this is closing player inventory after cancelling the event
 */
@Deprecated
public class PlayerBuyMerchantRecipeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private InventoryClickEvent clickEvent;

    public PlayerBuyMerchantRecipeEvent(InventoryClickEvent event) {
        super((Player) event.getWhoClicked());
        this.clickEvent = event;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    private static int getTradeAmount(InventoryClickEvent event) {
        if (event.getClickedInventory().getItem(2) == null
                || event.getClickedInventory().getItem(2).getType() == Material.AIR)
            return 0;
        switch (event.getClick()) {
            case LEFT:
            case RIGHT: {
                if (event.getCursor() == null || event.getCursor().getType() == Material.AIR)
                    return 1;
                if (!event.getCursor().isSimilar(event.getCurrentItem()))
                    return 0;// nothing is done
                if (event.getCursor().getMaxStackSize() - event.getCursor().getAmount() < event.getCurrentItem()
                        .getAmount())
                    return 0;// nothing is done
                return 1;
            }
            case SHIFT_LEFT:
            case SHIFT_RIGHT: {
                MerchantInventory merc = ((MerchantInventory) event.getClickedInventory());
                MerchantRecipe recipe = merc.getSelectedRecipe();
                int maxTrades = recipe.getMaxUses() - recipe.getUses();// free to buy
                for (ItemStack item : recipe.getIngredients()) {
                    if (item == null || item.getType() == Material.AIR)
                        continue;
                    int itemPrice;
                    if (!event.getWhoClicked().hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE))
                        itemPrice = item.getAmount();
                    else
                        itemPrice = (int) Math.max(1, item.getAmount() - Math.max(1, Math.floor(item.getAmount() * (0.3 + (0.0625 * (event.getWhoClicked().getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE).getAmplifier()))))));
                    if (merc.getItem(0) != null && merc.getItem(0).isSimilar(item))
                        maxTrades = Math.min(maxTrades, merc.getItem(0).getAmount() / itemPrice);
                    else if (merc.getItem(1) != null && merc.getItem(1).isSimilar(item))
                        maxTrades = Math.min(maxTrades, merc.getItem(1).getAmount() / itemPrice);
                }
                int counter = 0;
                int maxCounter = maxTrades * event.getCurrentItem().getAmount();
                Inventory subInv = event.getView().getBottomInventory();
                for (int i = 0; i < subInv.getSize(); i++) {
                    if (subInv.getItem(i) == null || subInv.getItem(i).getType() == Material.AIR)
                        counter = counter + event.getCurrentItem().getMaxStackSize();
                    else if (subInv.getItem(i).isSimilar(event.getCurrentItem()))
                        counter = counter
                                + (Math.max(0, subInv.getItem(i).getMaxStackSize() - subInv.getItem(i).getAmount()));
                    else
                        continue;
                    if (counter >= maxCounter)
                        break;
                }
                if (counter < maxCounter)
                    maxTrades = counter % event.getCurrentItem().getAmount() == 0
                            ? counter / event.getCurrentItem().getAmount()
                            : counter / event.getCurrentItem().getAmount() + 1;
                return maxTrades;
            }
            default:
                return 0;
        }

    }

    public MerchantInventory getMerchantInventory() {
        return (MerchantInventory) clickEvent.getView().getTopInventory();
    }

    @Override
    public boolean isCancelled() {
        return clickEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        clickEvent.setCancelled(cancelled);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Get amount of expected transactions.
     * Usually the amount of reiceved items is equals or less than (result item amount * amount of transcations)
     * and is always bigger or equals than (result item amount * (amount of transcations - 1) +1)
     *
     * @return amount of transactions expected for this event, changing player inventory and ingredients may change this result
     */
    public int getTradingTimes() {
        return getTradeAmount(this.clickEvent);
    }

}
