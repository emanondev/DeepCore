package emanondev.core.gui;

import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import emanondev.core.CoreMain;
import emanondev.core.Hooks;

public class GuiHandler implements Listener {
	private static final HashMap<Player, Gui> latestUsedGui = new HashMap<>();

	public static Gui getLastUsedGui(Player p) {
		return latestUsedGui.get(p);
	}
	
	public GuiHandler() {
		if (Hooks.isEnabled("ProtocolLib")) {
			try {
			new NameChangeHandler();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private static void onOpen(InventoryOpenEvent event) {
		if (!(event.getView().getTopInventory().getHolder() instanceof Gui gui))
			return;
		if (!(event.getPlayer() instanceof Player))
			return;
		gui.onOpen(event);
		latestUsedGui.put((Player) event.getPlayer(), gui);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private static void onClick(InventoryClickEvent event) {
		if (!(event.getView().getTopInventory().getHolder() instanceof Gui holder))
			return;
		event.setCancelled(true);
		if (event.getWhoClicked() instanceof Player)
			holder.onClick(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private static void onDrag(InventoryDragEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof Gui) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private static void onClose(InventoryCloseEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof Gui)
			((Gui) event.getView().getTopInventory().getHolder()).onClose(event);
	}

	public static final Set<Gui> timerUpdated = Collections.newSetFromMap(new WeakHashMap<>());
	
	public static void registerTimerUpdatedGui(@NotNull Gui gui) {
		timerUpdated.add(gui);
	}
	public static void unregisterTimerUpdatedGui(@NotNull Gui gui) {
		timerUpdated.remove(gui);
	}
	
	public static final BukkitTask timerSeconds = new BukkitRunnable() {
		@Override
		public void run() {
			long time = System.currentTimeMillis();
			timerUpdated.forEach((g)->{
				if (g.getInventory().getViewers().size()>0)
					g.updateInventory();
			});
			time = System.currentTimeMillis()-time;
			if (time>8)
				CoreMain.get().logIssue("GuiHandler used &e"+time+" &fms to update &e"+timerUpdated.size()+"&f gui with timer");
		}
	}.runTaskTimer(CoreMain.get(),100L,20L);
	

}