package emanondev.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author utente
 *
 * called when a player click the result item of the anvil.
 * this event may be called event if the player do not get the item
 * ex: the player has full inventory
 */
@Deprecated
public class AnvilResultCreationEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private InventoryClickEvent clickEvent;

	public AnvilResultCreationEvent(InventoryClickEvent event) {
		super((Player) event.getWhoClicked());
		this.clickEvent = event;
	}
	
	public AnvilInventory getAnvil() {
		return (AnvilInventory) clickEvent.getClickedInventory();
	}
	public ItemStack getResult() {
		return clickEvent.getCurrentItem();
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

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

}
