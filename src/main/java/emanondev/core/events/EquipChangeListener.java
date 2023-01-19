package emanondev.core.events;

import emanondev.core.CoreMain;
import emanondev.core.UtilsInventory;
import emanondev.core.events.EquipmentChangeEvent.EquipMethod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EquipChangeListener implements Listener {
    private final HashMap<Player, EnumMap<EquipmentSlot, ItemStack>> equips = new HashMap<>();
    private static final long timerCheckFrequencyTicks = 40; // at least 20;
    private static final int maxCheckedPlayerPerTick = 6;//not too low to avoid Thread switch overprice
    private TimerCheckTask timerTask = null;
    private final HashSet<Player> clickDrop = new HashSet<>();
    private final CoreMain plugin;

    public EquipChangeListener() {
        this.plugin = CoreMain.get();
        this.load();
    }

    private boolean isValidUser(Player e) {
        return !e.hasMetadata("BOT") && !e.hasMetadata("NPC");
    }

    private void loadUser(Player p) {
        if (!isValidUser(p))
            return;
        EnumMap<EquipmentSlot, ItemStack> map = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = p.getInventory().getItem(slot);
            if (item != null) //may be null
                map.put(slot, new ItemStack(item));
        }
        equips.put(p, map);
    }

    private void load() {
        if (timerTask != null)
            timerTask.cancel();
        equips.clear();
        for (Player p : Bukkit.getOnlinePlayers())
            loadUser(p);
        timerTask = new TimerCheckTask();
        timerTask.runTaskTimer(plugin, timerCheckFrequencyTicks, timerCheckFrequencyTicks);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void event(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (event.getPlayer().isOnline())
                        loadUser(event.getPlayer());
                }
                , 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void event(PlayerQuitEvent event) {
        equips.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void event(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p))
            return;
        if (!isValidUser(p))
            return;
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
                if (slot != null && UtilsInventory.isAirOrNull(p.getInventory().getItem(slot)))
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
                        if (event.getCursor().isSimilar(p.getInventory().getItem(slot)))
                            slots.add(slot);
                    }
                else if (event.getCursor().isSimilar(p.getInventory().getItem(EquipmentSlot.HAND)))
                    slots.add(EquipmentSlot.HAND);
                if (slots.size() > 0)
                    new SlotCheck(p, EquipMethod.INVENTORY_COLLECT_TO_CURSOR, slots).runTaskLater(plugin, 1L);
                return;
            case PICKUP_SOME:
            case UNKNOWN:
                return; // ???
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) // compatibility -> !=priority
    private void event(PlayerInteractEvent e) {
        if (UtilsInventory.isAirOrNull(e.getItem()))
            return;
        if (e.useItemInHand() == Result.DENY)
            return;
        if (!isValidUser(e.getPlayer()))
            return;
        EquipmentSlot type = guessRightClickSlotType(e.getItem());
        switch (e.getAction()) {
            case RIGHT_CLICK_AIR:
                if (type != null && UtilsInventory.isAirOrNull(e.getPlayer().getInventory().getItem(type))) {
                    onEquipChange(e.getPlayer(), EquipMethod.RIGHT_CLICK, type, null, e.getItem());
                    onEquipChange(e.getPlayer(), EquipMethod.RIGHT_CLICK, e.getHand(), e.getItem(), null);
                } else if (e.getItem().getAmount() == 1)
                    new SlotCheck(e.getPlayer(), EquipMethod.USE, e.getHand()).runTaskLater(plugin, 1L);
                return;
            case RIGHT_CLICK_BLOCK:
                if (e.useItemInHand() == Result.DENY)
                    return;
                if (type != null && UtilsInventory.isAirOrNull(e.getPlayer().getInventory().getItem(type)))
                    new SlotCheck(e.getPlayer(), EquipMethod.RIGHT_CLICK, e.getHand(), type).runTaskLater(plugin, 1L);
                else if (e.getItem().getAmount() == 1)
                    new SlotCheck(e.getPlayer(), EquipMethod.USE, e.getHand()).runTaskLater(plugin, 1L);
            default:
                return;
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void event(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player p))
            return;
        if (!isValidUser(p))
            return;
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
        if (!isValidUser(event.getPlayer()))
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
        if (!isValidUser(e.getPlayer()))
            return;
        ArrayList<EquipmentSlot> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (e.getBrokenItem().equals(e.getPlayer().getInventory().getItem(slot)))
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
        if (!isValidUser(event.getEntity()))
            return;
        for (EquipmentSlot type : EquipmentSlot.values()) {
            ItemStack item = event.getEntity().getInventory().getItem(type);
            if (!UtilsInventory.isAirOrNull(item))
                onEquipChange(event.getEntity(), EquipMethod.DEATH, type, item, null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerItemConsumeEvent event) {
        if (!isValidUser(event.getPlayer()))
            return;
        if (event.getItem().getAmount() != 1)
            return;
        List<EquipmentSlot> slots = new ArrayList<>(1);
        if (event.getItem().equals(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND)))
            slots.add(EquipmentSlot.HAND);
        if (event.getItem().equals(event.getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND)))
            slots.add(EquipmentSlot.OFF_HAND);
        if (slots.size() == 1)
            onEquipChange(event.getPlayer(), EquipMethod.CONSUME, slots.get(0), event.getItem(),
                    event.getItem().getType() == Material.MILK_BUCKET ? new ItemStack(Material.BUCKET) : null);
        else if (slots.size() > 1)
            new SlotCheck(event.getPlayer(), EquipMethod.CONSUME, slots).runTaskLater(plugin, 1L);
        else
            throw new IllegalStateException(event.getItem().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void event(PlayerDropItemEvent event) {
        if (!isValidUser(event.getPlayer()))
            return;
        if (clickDrop.remove(event.getPlayer()))
            return;
        if (event.isCancelled())
            return;
        if (UtilsInventory.isAirOrNull(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND)))
            onEquipChange(event.getPlayer(), EquipMethod.DROP, EquipmentSlot.HAND, event.getItemDrop().getItemStack(),
                    null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerInteractEntityEvent event) {
        if (!isValidUser(event.getPlayer()))
            return;
        ItemStack handItem = event.getPlayer().getInventory().getItem(event.getHand());
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

        if (event.getRightClicked() instanceof Sheep sheep) {
            if (sheep.isSheared())
                return;
            if (!handItem.getType().name().endsWith("_DYE"))
                return;
            if (handItem.getType().name().equals(sheep.getColor().name() + "_DYE"))
                return;
            onEquipChange(event.getPlayer(), EquipMethod.SHEEP_COLOR, event.getHand(), handItem, null);
            return;
        }
        new SlotCheck(event.getPlayer(), EquipMethod.USE_ON_ENTITY, event.getHand()).runTaskLater(plugin, 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerArmorStandManipulateEvent event) {
        if (!isValidUser(event.getPlayer()))
            return;
        if (UtilsInventory.isSimilarIgnoreDamage(event.getArmorStandItem(), event.getPlayerItem()))
            return;
        onEquipChange(event.getPlayer(), EquipMethod.ARMOR_STAND_MANIPULATE, event.getHand(), event.getPlayerItem(),
                event.getArmorStandItem());
    }

    @EventHandler
    private void event(PlayerItemHeldEvent event) {
        if (!isValidUser(event.getPlayer()))
            return;
        ItemStack i1 = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack i2 = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (i1 == i2)
            return;
        if (i1 != null && i1.isSimilar(i2))
            return;
        onEquipChange(event.getPlayer(), EquipMethod.HOTBAR_HAND_CHANGE, EquipmentSlot.HAND, i1, i2);
    }

    @EventHandler
    private void event(PlayerRespawnEvent event) {
        if (!isValidUser(event.getPlayer()))
            return;
        for (EquipmentSlot type : EquipmentSlot.values()) {
            ItemStack item = event.getPlayer().getInventory().getItem(type);
            if (!UtilsInventory.isAirOrNull(item))
                onEquipChange(event.getPlayer(), EquipMethod.RESPAWN, type, null, item);
        }
    }

    @EventHandler
    private void event(BlockDispenseArmorEvent event) {
        if (!(event.getTargetEntity() instanceof Player p))
            return;
        if (!isValidUser(p))
            return;
        EquipmentSlot slot = guessDispenserSlotType(event.getItem());
        if (slot == null)
            throw new IllegalStateException(event.getItem().toString());
        onEquipChange(p, EquipMethod.DISPENSER, slot, null, event.getItem());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player p))
            return;
        if (!isValidUser(p))
            return;

        if (!UtilsInventory.isAirOrNull(p.getInventory().getItem(EquipmentSlot.HAND)))
            return;
        for (int i = 0; i < p.getInventory().getHeldItemSlot(); i++)
            if (UtilsInventory.isAirOrNull(p.getInventory().getItem(i)))
                return;
        new SlotCheck(p, EquipMethod.PICKUP, EquipmentSlot.HAND).runTaskLater(plugin, 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerTeleportEvent event) {
        if (!isValidUser(event.getPlayer()))
            return;
        new SlotCheck(event.getPlayer(), EquipMethod.PLUGIN_WORLD_CHANGE, EquipmentSlot.values()).runTaskLater(plugin,
                1L);
    }


    private void onEquipChange(Player p, EquipMethod reason, EquipmentSlot type, ItemStack oldItem, ItemStack newItem) {
        equips.get(p).put(type, UtilsInventory.isAirOrNull(newItem) ? null : new ItemStack(newItem));
        Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(p, reason, type, oldItem, newItem));
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
            return switch (slot) {
                case HAND -> p.getInventory().getHeldItemSlot() + 36;
                case HEAD -> 5;
                case CHEST -> 6;
                case LEGS -> 7;
                case FEET -> 8;
                case OFF_HAND -> 45;
            };
        if (slot == EquipmentSlot.HAND)
            return p.getInventory().getHeldItemSlot() + view.getTopInventory().getSize() + 27;

        return -1;
    }

    private static EquipmentSlot getEquipmentSlotAtPosition(int pos, Player p, InventoryView view) {
        if (view.getTopInventory().getType() == InventoryType.CRAFTING)
            return switch (pos) {
                case 5 -> EquipmentSlot.HEAD;
                case 6 -> EquipmentSlot.CHEST;
                case 7 -> EquipmentSlot.LEGS;
                case 8 -> EquipmentSlot.FEET;
                case 45 -> EquipmentSlot.OFF_HAND;
                default -> p.getInventory().getHeldItemSlot() + 36 == pos ? EquipmentSlot.HAND : null;
            };
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
                        if (!isValidUser(p))
                            continue;
                        if (p.isOnline())
                            loadUser(p);
                        //new IllegalStateException().printStackTrace();
                        continue;
                    }
                    counter++;
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        ItemStack newItem = p.getInventory().getItem(slot);
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

        private SlotCheck(@NotNull Player p, @NotNull EquipMethod method, @NotNull EquipmentSlot... slots) {
            this.p = p;
            this.method = method;
            if (!equips.containsKey(p)) {
                if (p.isOnline())
                    loadUser(p);
                return;
            }
            for (EquipmentSlot slot : slots) {
                if (!UtilsInventory.isSimilarIgnoreDamage(equips.get(p).get(slot), p.getInventory().getItem(slot)))
                    onEquipChange(p, EquipMethod.UNKNOWN, slot, equips.get(p).get(slot), p.getInventory().getItem(slot));
                this.slots.add(slot);
            }
        }

        public SlotCheck(@NotNull Player p, @NotNull EquipMethod method, @NotNull Collection<EquipmentSlot> slots) {
            if (slots.size() == 0)
                throw new IllegalArgumentException();
            this.p = p;
            this.method = method;
            if (!equips.containsKey(p)) {
                if (p.isOnline())
                    loadUser(p);
                return;
            }
            for (EquipmentSlot slot : slots) {
                ItemStack equip = p.getInventory().getItem(slot);
                equip = UtilsInventory.isAirOrNull(equip) ? null : new ItemStack(equip);
                if (!UtilsInventory.isSimilarIgnoreDamage(equips.get(p).get(slot), equip))
                    onEquipChange(p, EquipMethod.UNKNOWN, slot, equips.get(p).get(slot), equip);
                this.slots.add(slot);
            }
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
                ItemStack item = p.getInventory().getItem(slot);
                if (UtilsInventory.isAirOrNull(item))
                    item = null;
                if (UtilsInventory.isSimilarIgnoreDamage(item, equips.get(p).get(slot))) {
                    continue;
                }
                onEquipChange(p, method, slot, equips.get(p).get(slot), item);
            }
        }

    }
}
