package emanondev.core.gui;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GuiHandler implements Listener {
	private static HashMap<Player,Gui> latestUsedGui = new HashMap<>();
	
	public static Gui getLastUsedGui(Player p) {
		return latestUsedGui.get(p);
	}
	

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	private static void onOpen(InventoryOpenEvent event) {
		if (!(event.getView().getTopInventory().getHolder() instanceof Gui))
			return;
		if (!(event.getPlayer() instanceof Player))
			return;
		latestUsedGui.put((Player) event.getPlayer(),(Gui) event.getView().getTopInventory().getHolder());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	private static void onClick(InventoryClickEvent event) {
		if (!(event.getView().getTopInventory().getHolder() instanceof Gui))
			return;
		event.setCancelled(true);
		//if (event.getClickedInventory()!=null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
			Gui holder = (Gui) event.getView().getTopInventory().getHolder();
			if (event.getWhoClicked() instanceof Player)
				holder.onClick(event);
		//}
	}
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	private static void onDrag(InventoryDragEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof Gui) {
			event.setCancelled(true);
			//if (event.getWhoClicked() instanceof Player)
				//holder.onDrag(event);
		}
	}
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	private static void onClose(InventoryCloseEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof Gui)
			((Gui) event.getView().getTopInventory().getHolder()).onClose(event);
	}

}