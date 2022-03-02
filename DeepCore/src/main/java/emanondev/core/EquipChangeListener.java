package emanondev.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import emanondev.core.events.EquipmentChangeEvent;
import emanondev.core.events.EquipmentChangeEvent.EquipMethod;

class EquipChangeListener implements Listener{
	private final HashMap<Player, EnumMap<EquipmentSlot, ItemStack>> equips = new HashMap<>();
	private static final long timerCheckFrequencyTicks = 40; // at least 20;
	private static final int maxCheckedPlayerPerTick = 6;//not too low to avoid Thread switch overprice
	private TimerCheckTask timerTask = null;
	private final HashSet<Player> clickDrop = new HashSet<>();
	private final CoreMain plugin;

	EquipChangeListener() {
		this.plugin = CoreMain.get();
		this.load();
	}

	private void load() {
		if (timerTask != null)
			timerTask.cancel();
		//timerCheckFrequencyTicks = Math.max(1, getConfInt("timer_check.frequency_seconds")) * 20;
		//maxCheckedPlayerPerTick = Math.max(1, getConfInt("timer_check.max_checked_players_per_tick"));
		equips.clear();
		for (Player p : Bukkit.getOnlinePlayers()) {
			EnumMap<EquipmentSlot, ItemStack> map = new EnumMap<>(EquipmentSlot.class);
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				ItemStack item = UtilsInventory.getEquip(p, slot);
				map.put(slot, item == null ? null : new ItemStack(item));
			}
			equips.put(p, map);
		}
		timerTask = new TimerCheckTask();
		timerTask.runTaskTimer(plugin, timerCheckFrequencyTicks, timerCheckFrequencyTicks);

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void event(PlayerJoinEvent event) {
		if (event.getPlayer().hasMetadata("BOT"))
			return;
		EnumMap<EquipmentSlot, ItemStack> map = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack item = UtilsInventory.getEquip(event.getPlayer(), slot);
			map.put(slot, item == null ? null : new ItemStack(item));
		}
		equips.put(event.getPlayer(), map);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void event(PlayerQuitEvent event) {
		equips.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void event(final InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		if (event.getWhoClicked().hasMetadata("NPC")||event.getWhoClicked().hasMetadata("BOT"))
			return;
		Player p = (Player) event.getWhoClicked();
		EquipmentSlot clickedSlot = getEquipmentSlotAtPosition(event.getRawSlot(), p, event.getView());

		switch (event.getAction()) {
		case CLONE_STACK:
		case NOTHING:
		case HOTBAR_MOVE_AND_READD:
			return;
		case DROP_ONE_CURSOR:
		case DROP_ALL_CURSOR:
			clickDrop.add(p);
			return;
		case DROP_ONE_SLOT:
			if (!UtilsInventory.isAirOrNull(event.getCursor()))
				return;
			if (clickedSlot != null && event.getCurrentItem().getAmount() == 1)
				onEquipChange(p, EquipMethod.INVENTORY_DROP, clickedSlot, event.getCurrentItem(), null);
			clickDrop.add(p);
			return;
		case DROP_ALL_SLOT:
			if (!UtilsInventory.isAirOrNull(event.getCursor()))
				return;
			if (clickedSlot != null)
				onEquipChange(p, EquipMethod.INVENTORY_DROP, clickedSlot, event.getCurrentItem(), null);

			clickDrop.add(p);
			return;
		case PICKUP_ALL:
			if (clickedSlot == null)
				return;
			onEquipChange(p, EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
			return;
		case PICKUP_HALF:
			if (clickedSlot == null)
				return;
			if (event.getCurrentItem().getAmount() > 1)
				return;
			onEquipChange(p, EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
			return;
		case PICKUP_ONE:
			if (clickedSlot == null)
				return;
			if (event.getCurrentItem().getAmount() != 1)
				return;
			onEquipChange(p, EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
			return;
		case PLACE_ALL:
		case PLACE_ONE:
		case PLACE_SOME:
			if (clickedSlot == null)
				return;
			if (UtilsInventory.isAirOrNull(event.getCurrentItem()))
				onEquipChange(p, EquipMethod.INVENTORY_PLACE, clickedSlot, null, event.getCursor());
			return;
		case SWAP_WITH_CURSOR:
			if (clickedSlot == null)
				return;
			if (UtilsInventory.isSimilarIgnoreDamage(event.getCurrentItem(), event.getCursor()))
				return;
			onEquipChange(p, EquipMethod.INVENTORY_SWAP_WITH_CURSOR, clickedSlot, event.getCurrentItem(),
					event.getCursor());
			return;
		case HOTBAR_SWAP: {
			ItemStack to = p.getInventory().getItem(event.getHotbarButton() == -1 ? 45 : event.getHotbarButton());
			if (UtilsInventory.isSimilarIgnoreDamage(event.getCurrentItem(), to))
				return;
			if (clickedSlot != null)
				onEquipChange(p, EquipMethod.INVENTORY_HOTBAR_SWAP, clickedSlot, event.getCurrentItem(), to);
			if (event.getHotbarButton() == -1)
				onEquipChange(p, EquipMethod.INVENTORY_HOTBAR_SWAP, EquipmentSlot.OFF_HAND, to, event.getCurrentItem());
			else if (event.getHotbarButton() == p.getInventory().getHeldItemSlot())
				onEquipChange(p, EquipMethod.INVENTORY_HOTBAR_SWAP, EquipmentSlot.HAND, to, event.getCurrentItem());
			return;
		}
		case MOVE_TO_OTHER_INVENTORY: {
			EquipmentSlot slot = event.getView().getTopInventory().getType() == InventoryType.CRAFTING
					? guessDispenserSlotType(event.getCurrentItem())
					: null;
			if (slot != null && UtilsInventory.isAirOrNull(UtilsInventory.getEquip(p, slot)))
				onEquipChange(p, EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, slot, null, event.getCurrentItem());
			if (clickedSlot == null || clickedSlot == EquipmentSlot.HAND)
				new SlotCheck(p, EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, EquipmentSlot.HAND)
						.runTaskLater(plugin, 1L);
			else
				new SlotCheck(p, EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, clickedSlot).runTaskLater(plugin,
						1L);
			return;
		}
		case COLLECT_TO_CURSOR:
			ArrayList<EquipmentSlot> slots = new ArrayList<>();
			if (event.getView().getTopInventory().getType() == InventoryType.CRAFTING)
				for (EquipmentSlot slot : EquipmentSlot.values()) {
					if (event.getCursor().isSimilar(UtilsInventory.getEquip(p, slot)))
						slots.add(slot);
				}
			else if (event.getCursor().isSimilar(UtilsInventory.getEquip(p, EquipmentSlot.HAND)))
				slots.add(EquipmentSlot.HAND);
			if (slots.size() > 0)
				new SlotCheck(p, EquipMethod.INVENTORY_COLLECT_TO_CURSOR, slots).runTaskLater(plugin, 1L);
			return;
		case PICKUP_SOME:
		case UNKNOWN:
			return; // ???
		}
	}

	@EventHandler(priority = EventPriority.MONITOR) // compability -> !=priority
	private void event(PlayerInteractEvent e) {
		if (UtilsInventory.isAirOrNull(e.getItem()))
			return;
		if (e.useItemInHand() == Result.DENY)
			return;
		if (e.getPlayer().hasMetadata("NPC")||e.getPlayer().hasMetadata("BOT"))
			return;
		EquipmentSlot type = guessRightClickSlotType(e.getItem());
		switch (e.getAction()) {
		case RIGHT_CLICK_AIR:
			if (type != null && UtilsInventory.isAirOrNull(UtilsInventory.getEquip(e.getPlayer(), type))) {
				onEquipChange(e.getPlayer(), EquipMethod.RIGHT_CLICK, type, null, e.getItem());
				onEquipChange(e.getPlayer(), EquipMethod.RIGHT_CLICK, e.getHand(), e.getItem(), null);
			} else if (e.getItem().getAmount() == 1)
				new SlotCheck(e.getPlayer(), EquipMethod.USE, e.getHand()).runTaskLater(plugin, 1L);
			return;
		case RIGHT_CLICK_BLOCK:
			if (e.useItemInHand() == Result.DENY)
				return;
			if (type != null && UtilsInventory.isAirOrNull(UtilsInventory.getEquip(e.getPlayer(), type))) {
				new SlotCheck(e.getPlayer(), EquipMethod.RIGHT_CLICK, e.getHand(), type).runTaskLater(plugin, 1L);
				// should check block type
				// onEquipChange(e.getPlayer(), EquipMethod.RIGHT_CLICK, type, null,
				// e.getItem());
				// onEquipChange(e.getPlayer(), EquipMethod.RIGHT_CLICK, e.getHand(),
				// e.getItem(), null);
			} else if (e.getItem().getAmount() == 1)
				new SlotCheck(e.getPlayer(), EquipMethod.USE, e.getHand()).runTaskLater(plugin, 1L);
		default:
			return;
		}

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void event(InventoryDragEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		if (event.getWhoClicked().hasMetadata("NPC")||event.getWhoClicked().hasMetadata("BOT"))
			return;
		Player p = (Player) event.getWhoClicked();
		for (EquipmentSlot type : EquipmentSlot.values()) {
			int pos = getSlotPosition(type, p, event.getView());
			if (pos != -1 && event.getNewItems().containsKey(pos)) {
				ItemStack itemOld = event.getView().getItem(pos);
				ItemStack itemNew = event.getNewItems().get(pos);
				if (!UtilsInventory.isSimilarIgnoreDamage(itemOld, itemNew))
					onEquipChange(p, EquipMethod.INVENTORY_DRAG, type, itemOld, itemNew);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void event(PlayerSwapHandItemsEvent event) {
		if (event.getPlayer().hasMetadata("NPC")||event.getPlayer().hasMetadata("BOT"))
			return;
		if (UtilsInventory.isSimilarIgnoreDamage(event.getMainHandItem(), event.getOffHandItem()))
			return;
		onEquipChange(event.getPlayer(), EquipMethod.SWAP_HANDS_ITEM, EquipmentSlot.HAND, event.getOffHandItem(),
				event.getMainHandItem());
		onEquipChange(event.getPlayer(), EquipMethod.SWAP_HANDS_ITEM, EquipmentSlot.OFF_HAND, event.getMainHandItem(),
				event.getOffHandItem());
	}

	@EventHandler
	private void event(PlayerItemBreakEvent e) {
		if (e.getPlayer().hasMetadata("NPC")||e.getPlayer().hasMetadata("BOT"))
			return;
		ArrayList<EquipmentSlot> slots = new ArrayList<>();
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (e.getBrokenItem().equals(UtilsInventory.getEquip(e.getPlayer(), slot)))
				slots.add(slot);
		}
		if (slots.size() == 0)
			throw new IllegalStateException();
		if (slots.size() == 1) {
			onEquipChange(e.getPlayer(), EquipMethod.BROKE, slots.get(0), e.getBrokenItem(), null);
			return;
		}
		new SlotCheck(e.getPlayer(), EquipMethod.BROKE, slots).runTaskLater(plugin, 1L);
	}

	@EventHandler
	private void event(PlayerDeathEvent event) {
		if (event.getEntity().hasMetadata("NPC")||event.getEntity().hasMetadata("BOT"))
			return;
		for (EquipmentSlot type : EquipmentSlot.values()) {
			ItemStack item = UtilsInventory.getEquip(event.getEntity(), type);
			if (!UtilsInventory.isAirOrNull(item))
				onEquipChange(event.getEntity(), EquipMethod.DEATH, type, item, null);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void event(PlayerItemConsumeEvent event) {
		if (event.getPlayer().hasMetadata("NPC")||event.getPlayer().hasMetadata("BOT"))
			return;
		if (event.getItem().getAmount() != 1)
			return;
		List<EquipmentSlot> slots = new ArrayList<>(1);
		if (event.getItem().equals(UtilsInventory.getEquip(event.getPlayer(), EquipmentSlot.HAND)))
			slots.add(EquipmentSlot.HAND);
		if (event.getItem().equals(UtilsInventory.getEquip(event.getPlayer(), EquipmentSlot.OFF_HAND)))
			slots.add(EquipmentSlot.OFF_HAND);
		if (slots.size() == 1)
			onEquipChange(event.getPlayer(), EquipMethod.CONSUME, slots.get(0), event.getItem(),
					event.getItem().getType() == Material.MILK_BUCKET ? new ItemStack(Material.BUCKET) : null);
		else if (slots.size() > 1)
			new SlotCheck(event.getPlayer(), EquipMethod.CONSUME, slots).runTaskLater(plugin, 1L);
		else
			throw new IllegalStateException(event.getItem().toString());
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	private void event(PlayerDropItemEvent event) {
		if (event.getPlayer().hasMetadata("NPC")||event.getPlayer().hasMetadata("BOT"))
			return;
		if (clickDrop.remove(event.getPlayer()) == true)
			return;
		if (event.isCancelled())
			return;
		if (UtilsInventory.isAirOrNull(UtilsInventory.getEquip(event.getPlayer(), EquipmentSlot.HAND)))
			onEquipChange(event.getPlayer(), EquipMethod.DROP, EquipmentSlot.HAND, event.getItemDrop().getItemStack(),
					null);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void event(PlayerInteractEntityEvent event) {
		if (event.getPlayer().hasMetadata("NPC")||event.getPlayer().hasMetadata("BOT"))
			return;
		ItemStack handItem = UtilsInventory.getEquip(event.getPlayer(), event.getHand());
		if (UtilsInventory.isAirOrNull(handItem) || handItem.getAmount() > 1
				|| (event.getRightClicked().getType() == EntityType.ARMOR_STAND
						&& handItem.getType() != Material.NAME_TAG))
			return;
		if (handItem.getType() == Material.NAME_TAG) {
			if (!(event.getRightClicked() instanceof LivingEntity) || (event.getRightClicked() instanceof Player))
				return;
			if (!handItem.hasItemMeta() || !handItem.getItemMeta().hasDisplayName())
				return;
			onEquipChange(event.getPlayer(), EquipMethod.NAMETAG_APPLY, event.getHand(), handItem, null);
			return;
		}

		if (event.getRightClicked() instanceof Sheep) {
			Sheep sheep = (Sheep) event.getRightClicked();
			if (sheep.isSheared())
				return;
			if (!handItem.getType().name().endsWith("_DYE"))
				return;
			if (handItem.getType().name().equals(sheep.getColor().name() + "_DYE"))
				return;
			onEquipChange(event.getPlayer(), EquipMethod.SHEEP_COLOR, event.getHand(), handItem, null);
			return;
		}
		new SlotCheck(event.getPlayer(),EquipMethod.USE_ON_ENTITY,event.getHand()).runTaskLater(plugin, 1L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void event(PlayerArmorStandManipulateEvent event) {
		if (event.getPlayer().hasMetadata("NPC")||event.getPlayer().hasMetadata("BOT"))
			return;
		if (UtilsInventory.isSimilarIgnoreDamage(event.getArmorStandItem(), event.getPlayerItem()))
			return;
		onEquipChange(event.getPlayer(), EquipMethod.ARMOR_STAND_MANIPULATE, event.getHand(), event.getPlayerItem(),
				event.getArmorStandItem());
	}

	@EventHandler
	private void event(PlayerItemHeldEvent event) {
		if (event.getPlayer().hasMetadata("NPC")||event.getPlayer().hasMetadata("BOT"))
			return;
		ItemStack i1 = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
		ItemStack i2 = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if (i1 == i2)
			return;
		if (i1 == null ? false : i1.isSimilar(i2))
			return;
		onEquipChange(event.getPlayer(), EquipMethod.HOTBAR_HAND_CHANGE, EquipmentSlot.HAND, i1, i2);
	}

	@EventHandler
	private void event(PlayerRespawnEvent event) {
		if (event.getPlayer().hasMetadata("NPC")||event.getPlayer().hasMetadata("BOT"))
			return;
		for (EquipmentSlot type : EquipmentSlot.values()) {
			ItemStack item = UtilsInventory.getEquip(event.getPlayer(), type);
			if (!UtilsInventory.isAirOrNull(item))
				onEquipChange(event.getPlayer(), EquipMethod.RESPAWN, type, null, item);
		}
	}

	@EventHandler
	private void event(BlockDispenseArmorEvent event) {
		if (!(event.getTargetEntity() instanceof Player))
			return;
		if (event.getTargetEntity().hasMetadata("NPC")||event.getTargetEntity().hasMetadata("BOT"))
			return;
		EquipmentSlot slot = guessDispenserSlotType(event.getItem());
		if (slot == null)
			throw new IllegalStateException(event.getItem().toString());
		onEquipChange((Player) event.getTargetEntity(), EquipMethod.DISPENSER, slot, null, event.getItem());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void event(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (event.getEntity().hasMetadata("NPC")||event.getEntity().hasMetadata("BOT"))
			return;
		Player p = (Player) event.getEntity();

		if (!UtilsInventory.isAirOrNull(UtilsInventory.getEquip(p, EquipmentSlot.HAND)))
			return;
		for (int i = 0; i < p.getInventory().getHeldItemSlot(); i++)
			if (UtilsInventory.isAirOrNull(p.getInventory().getItem(i)))
				return;
		new SlotCheck(p, EquipMethod.PICKUP, EquipmentSlot.HAND).runTaskLater(plugin, 1L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void event(PlayerTeleportEvent event) {
		if (event.getPlayer().hasMetadata("NPC")||event.getPlayer().hasMetadata("BOT"))
			return;
		new SlotCheck(event.getPlayer(), EquipMethod.PLUGIN_WORLD_CHANGE, EquipmentSlot.values()).runTaskLater(plugin,
				1L);
	}

	


	private void onEquipChange(Player p, EquipMethod reason, EquipmentSlot type, ItemStack oldItem, ItemStack newItem) {
		equips.get(p).put(type, UtilsInventory.isAirOrNull(newItem) ? null : new ItemStack(newItem));
		Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(p,reason,type,oldItem,newItem));
		//Bukkit.broadcastMessage("§a" + reason.name() + " " + type.name() + " for " + p.getName() + " "
		//		+ (UtilsInventory.isAirOrNull(oldItem) ? "null" : oldItem.getType().name()) + " "
		//		+ (UtilsInventory.isAirOrNull(newItem) ? "null" : newItem.getType().name()));
	}

	private static EquipmentSlot guessRightClickSlotType(ItemStack item) {
		if (UtilsInventory.isAirOrNull(item))
			return null;
		String type = item.getType().name();
		if (type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("_HEAD"))
			return EquipmentSlot.HEAD;
		else if (type.endsWith("_CHESTPLATE") || type.equals("ELYTRA"))
			return EquipmentSlot.CHEST;
		else if (type.endsWith("_LEGGINGS"))
			return EquipmentSlot.LEGS;
		else if (type.endsWith("_BOOTS"))
			return EquipmentSlot.FEET;
		else
			return null;
	}

	private static EquipmentSlot guessDispenserSlotType(ItemStack item) {
		EquipmentSlot slot = guessRightClickSlotType(item);
		if (slot == null && item != null) {
			if (item.getType().name().endsWith("PUMPKIN"))
				return EquipmentSlot.HEAD;
			else if (item.getType() == Material.SHIELD)
				return EquipmentSlot.OFF_HAND;
		}
		return slot;
	}

	private static int getSlotPosition(EquipmentSlot slot, Player p, InventoryView view) {
		if (view.getTopInventory().getType() == InventoryType.CRAFTING)
			switch (slot) {
			case HAND:
				return p.getInventory().getHeldItemSlot() + 36;
			case HEAD:
				return 5;
			case CHEST:
				return 6;
			case LEGS:
				return 7;
			case FEET:
				return 8;
			case OFF_HAND:
				return 45;
			default:
				return -1;
			}
		switch (slot) {
		case HAND:
			return p.getInventory().getHeldItemSlot() + view.getTopInventory().getSize() + 27;
		default:
			return -1;
		}
	}

	private static EquipmentSlot getEquipmentSlotAtPosition(int pos, Player p, InventoryView view) {
		if (view.getTopInventory().getType() == InventoryType.CRAFTING)
			switch (pos) {
			case 5:
				return EquipmentSlot.HEAD;
			case 6:
				return EquipmentSlot.CHEST;
			case 7:
				return EquipmentSlot.LEGS;
			case 8:
				return EquipmentSlot.FEET;
			case 45:
				return EquipmentSlot.OFF_HAND;
			default:
				return p.getInventory().getHeldItemSlot() + 36 == pos ? EquipmentSlot.HAND : null;
			}
		return pos == p.getInventory().getHeldItemSlot() + view.getTopInventory().getSize() + 27 ? EquipmentSlot.HAND
				: null;
	}

	private class TimerCheckTask extends BukkitRunnable {
	
		private PlayerCheck subTask = null;
	
		@Override
		public void run() {
			if (subTask == null || subTask.isCancelled())
				if (Bukkit.getOnlinePlayers().size() > 0) {
					subTask = new PlayerCheck();
					subTask.runTaskTimer(plugin, 1L, 1L);
				}
		}
	
		public void cancel() {
			super.cancel();
			if (subTask != null)// && !subTask.isCancelled())
				subTask.cancel();
		}
	
		private class PlayerCheck extends BukkitRunnable {
			private final List<Player> players = new ArrayList<>();
			private int index = 0;
	
			private PlayerCheck() {
				players.addAll(Bukkit.getOnlinePlayers());
			}
	
			@Override
			public void run() {
				int counter = 0;
				while (counter < maxCheckedPlayerPerTick) {
					if (index >= players.size()) {
						this.cancel();
						return;
					}
					Player p = players.get(index);
					index++;
					if (!p.isOnline())
						continue;
					if (!equips.containsKey(p)) {
						if (p.hasMetadata("BOT"))
							continue;
						new IllegalStateException().printStackTrace();
						continue;
					}
					counter++;
					for (EquipmentSlot slot : EquipmentSlot.values()) {
						ItemStack newItem = UtilsInventory.getEquip(p, slot);
						ItemStack oldItem = equips.get(p).get(slot);
						if (!UtilsInventory.isSimilarIgnoreDamage(oldItem, newItem))
							onEquipChange(p, EquipMethod.UNKNOWN, slot, oldItem, newItem);
					}
	
				}
	
			}
	
		}
	
	}

	private class SlotCheck extends BukkitRunnable {

		private final EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
		private final Player p;
		private final EquipMethod method;

		private SlotCheck(Player p, EquipMethod method, EquipmentSlot... slots) {
			if (slots == null || slots.length == 0 || p == null || method == null)
				throw new IllegalArgumentException();
			this.p = p;
			this.method = method;
			if (!equips.containsKey(p)) {
				new IllegalStateException().printStackTrace();
				return;
			}
			for (EquipmentSlot slot : slots) {
				if (!UtilsInventory.isSimilarIgnoreDamage(equips.get(p).get(slot), UtilsInventory.getEquip(p, slot)))
					onEquipChange(p, EquipMethod.UNKNOWN, slot, equips.get(p).get(slot), UtilsInventory.getEquip(p, slot));
				this.slots.add(slot);
			}
			return;
		}

		public SlotCheck(Player p, EquipMethod method, Collection<EquipmentSlot> slots) {
			if (slots == null || slots.size() == 0 || p == null || method == null)
				throw new IllegalArgumentException();
			this.p = p;
			this.method = method;
			if (!equips.containsKey(p)) {
				new IllegalStateException().printStackTrace();
				return;
			}
			for (EquipmentSlot slot : slots) {
				ItemStack equip = UtilsInventory.getEquip(p, slot);
				if (UtilsInventory.isAirOrNull(equip))
					equip = null;
				else
					equip = new ItemStack(equip);
				if (!UtilsInventory.isSimilarIgnoreDamage(equips.get(p).get(slot), equip))
					onEquipChange(p, EquipMethod.UNKNOWN, slot, equips.get(p).get(slot), equip);
				this.slots.add(slot);
			}
			return;
		}

		@Override
		public void run() {
			if (!p.isOnline())
				return;
			if (!equips.containsKey(p)) {
				new IllegalStateException().printStackTrace();
				return;
			}
			for (EquipmentSlot slot : slots) {
				ItemStack item = UtilsInventory.getEquip(p, slot);
				if (UtilsInventory.isAirOrNull(item))
					item = null;
				if (UtilsInventory.isSimilarIgnoreDamage(item, equips.get(p).get(slot))) {
					continue;
				}
				onEquipChange(p, method, slot, equips.get(p).get(slot), item);
			}
			return;
		}

	}
}
