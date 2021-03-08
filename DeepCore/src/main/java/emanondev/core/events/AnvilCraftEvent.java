package emanondev.core.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AnvilCraftEvent extends CraftEvent {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	AnvilCraftEvent(InventoryClickEvent clickEvent) {
		super(clickEvent);
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

}