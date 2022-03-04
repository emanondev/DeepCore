package emanondev.core.events;

import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called each time a player try to buy something from a merchant.<br><br>
 * 
 * Event is also called if player buy none because inventory is too full or has not enough ingredients.<br>
 * Cancelling this event prevents player from buying stuffs but also glitch clientside,<br>
 * might show as locked some trades after cancelling them,<br>
 * best solution to avoid this is closing player inventory after cancelling the event
 */
public class MerchantCraftEvent extends CraftEvent {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	MerchantCraftEvent(InventoryClickEvent clickEvent) {
		super(clickEvent);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	public MerchantInventory getMerchantInventory() {
		return (MerchantInventory) this.getClickEvent().getView().getTopInventory();
	}
	
	/*
	 * https://www.spigotmc.org/threads/villager-trade-item-prices.487937/
	 * https://minecraft.gamepedia.com/Trading#Economics
	 * 
	 * counts only HeroOfTheVillage
	 * 
	 * ignore Demand/Offer & Popularity discounts
	 */
	@Override
	protected int getShiftMaxCraftableAmount() {
		MerchantInventory merc = getMerchantInventory();
		MerchantRecipe recipe = merc.getSelectedRecipe();

		int maxTrades = recipe.getMaxUses() - recipe.getUses();// free to buy
		
		for (ItemStack item : recipe.getIngredients()) {
			if (item == null || item.getType() == Material.AIR)
				continue;
			int itemPrice = (int) Math.max(1,item.getAmount()*recipe.getPriceMultiplier());
			
			/*
			//base price + heroOfVillageEffect
			if (!getClickEvent().getWhoClicked().hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE))
				itemPrice = item.getAmount();
			else
				itemPrice = (int) Math.max(1,
						item.getAmount() - Math.max(1,
								Math.floor(item.getAmount() * (0.3 + (0.0625 * (getClickEvent().getWhoClicked()
										.getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE).getAmplifier()))))));
			//Demand/Offer & Popularity discounts
			*/
			
			//how many recipes could fit?
			if (merc.getItem(0) != null && merc.getItem(0).isSimilar(item))
				maxTrades = Math.min(maxTrades, merc.getItem(0).getAmount() / itemPrice);
			else if (merc.getItem(1) != null && merc.getItem(1).isSimilar(item))
				maxTrades = Math.min(maxTrades, merc.getItem(1).getAmount() / itemPrice);
			else
				maxTrades = 0;
		}
		return maxTrades*getResult().getAmount();

	}

}
