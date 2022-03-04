package emanondev.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

abstract class CraftEvent extends InventoryEvent implements Cancellable {
	private final InventoryClickEvent clickEvent;

	CraftEvent(InventoryClickEvent clickEvent) {
		super(clickEvent.getView());
		this.clickEvent = clickEvent;
	}

	@Override
	public boolean isCancelled() {
		return clickEvent.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancelled) {
		clickEvent.setCancelled(cancelled);
	}

	public InventoryClickEvent getClickEvent() {
		return clickEvent;
	}

	public int getExpectedCraftedAmount() {

		switch (clickEvent.getClick()) {
		case LEFT:
		case RIGHT: {
			if (clickEvent.getCursor() != null && !clickEvent.getCursor().getType().isAir()) {
				if (!clickEvent.getCurrentItem().isSimilar(clickEvent.getCursor()))
					return 0;
				if (clickEvent.getCursor().getMaxStackSize() - clickEvent.getCursor().getAmount() < clickEvent
						.getCurrentItem().getAmount())
					return 0;
			}
			return clickEvent.getCurrentItem().getAmount();
		}
		case SHIFT_LEFT:
		case SHIFT_RIGHT: {

			int maxCraftable = getShiftMaxCraftableAmount();
			if (maxCraftable <= 0)
				return 0;

			// calcolo gli slot disponibili
			int possible = 0;
			for (ItemStack item:getView().getBottomInventory().getStorageContents()) {
				if (item == null || item.getType().isAir())
					possible = possible + this.getResult().getMaxStackSize();
				else if (item.isSimilar(this.getResult()))
					possible = possible + Math.max(0, this.getResult().getMaxStackSize() - item.getAmount());
			}

			if (possible == 0)
				return 0;

			if (maxCraftable <= possible)
				return maxCraftable;

			int given = 0;
			while (given < possible) {
				given = given + this.getResult().getAmount();
			}
			return Math.min(given, possible);
			/*
			 * while (maxCraftable > 0 && maxCraftable > possible) { given = maxCraftable;
			 * maxCraftable = maxCraftable - clickEvent.getCurrentItem().getAmount(); if
			 * (maxCraftable <= possible) return given; } return given;
			 */
		}
		case NUMBER_KEY: {
			ItemStack barItem = getView().getBottomInventory().getItem(clickEvent.getHotbarButton());
			if (barItem != null && !barItem.getType().isAir())
				return 0;
			return clickEvent.getCurrentItem().getAmount();
		}
		case DROP: {
			if (clickEvent.getCurrentItem() == null || clickEvent.getCurrentItem().getType().isAir())
				return 0;
			return clickEvent.getCurrentItem().getAmount();
		}
		default:
			return 0;
		}
	}

	/**
	 * returns the amount of how many item you may craft with a shift click with no
	 * inventory space limits
	 * 
	 * @return the amount of how many item you may craft with a shift click with no inventory space limits
	 */
	protected int getShiftMaxCraftableAmount() {
		int maxCraftable = -1;
		for (int slot = 0; slot < getView().getTopInventory().getSize(); slot++) {
			if (getView().getSlotType(slot) != SlotType.CRAFTING)
				continue;
			ItemStack craftingItem = getView().getItem(slot);
			if (craftingItem == null || craftingItem.getType().isAir())
				continue;
			if (maxCraftable == -1)
				maxCraftable = craftingItem.getAmount();
			else
				maxCraftable = Math.min(maxCraftable, craftingItem.getAmount());
		}
		maxCraftable = maxCraftable * clickEvent.getCurrentItem().getAmount();
		return maxCraftable;

	}

	public int getExpectedRecipeUses() {
		if (clickEvent.getCurrentItem() == null || clickEvent.getCurrentItem().getType().isAir()
				|| clickEvent.getCurrentItem().getAmount() == 0)
			return 0;
		return (int) Math.ceil(((double) getExpectedCraftedAmount()) / clickEvent.getCurrentItem().getAmount());
	}

	public ItemStack getResult() {
		return clickEvent.getCurrentItem();
	}

	public Player getPlayer() {
		return (Player) clickEvent.getWhoClicked();
	}

}