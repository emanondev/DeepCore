package emanondev.core.utility;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InventoryUtility {

    private static final Map<Class<?>, Method> getTopInventory =
            VersionUtility.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
    private static final Map<Class<?>, Method> getBottomInventory =
            VersionUtility.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();

    private InventoryUtility() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the top Inventory object from the event's InventoryView.<br><br>
     * This method may use reflections to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In API versions 1.21 and later, it is an interface.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The top Inventory object from the event's InventoryView.
     */
    public static Inventory getTopInventory(@NotNull InventoryEvent event) {
        if (VersionUtility.isNewerEquals(1, 21, 0))
            return event.getView().getTopInventory();
        return getTopInventoryP(event.getView());
    }

    /**
     * Returns the top Inventory object from the player's InventoryView.<br><br>
     * This method may use reflections to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In versions 1.21 and later, it is an interface.
     *
     * @param player The player with an InventoryView to inspect.
     * @return The top Inventory object from the player's InventoryView.
     */
    public static Inventory getTopInventory(@NotNull Player player) {
        if (VersionUtility.isNewerEquals(1, 21, 0))
            return player.getOpenInventory().getTopInventory();
        return getTopInventoryP(player.getOpenInventory());
    }

    private static Inventory getTopInventoryP(@NotNull Object view) {
        Method method = getTopInventory.get(view.getClass());
        if (method == null) {
            method = ReflectionUtility.getMethod(view.getClass(), "getTopInventory");
            getTopInventory.put(view.getClass(), method);
        }
        return (Inventory) ReflectionUtility.invokeMethod(view, method);
    }


    /**
     * Returns the bottom Inventory object from the event's InventoryView.<br><br>
     * This method may use reflections to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In API versions 1.21 and later, it is an interface.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The bottom Inventory object from the event's InventoryView.
     */
    public static Inventory getBottomInventory(@NotNull InventoryEvent event) {
        if (VersionUtility.isNewerEquals(1, 21, 0))
            return event.getView().getBottomInventory();
        return getBottomInventoryP(event.getView());
    }

    private static Inventory getBottomInventoryP(@NotNull Object view) {
        Method method = getBottomInventory.get(view.getClass());
        if (method == null) {
            method = ReflectionUtility.getMethod(view.getClass(), "getBottomInventory");
            getBottomInventory.put(view.getClass(), method);
        }
        return (Inventory) ReflectionUtility.invokeMethod(view, method);
    }


    /**
     * Update InventoryView for player.<br><br>
     * In API versions 1.19.3 and earlier, there is no implicit consistency for inventory and
     * changes, so it has to be done manually (also Purpur has similar issue in later versions too).<br>
     * In API versions 1.19.4 and later, there is implicit consistency for inventory and changes.
     *
     * @param player The player which inventory view should be updated
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void updateView(@NotNull Player player) {
        if (VersionUtility.isOlderEquals(1, 19, 4) || VersionUtility.hasPurpurAPI()) {
            SchedulerUtility.run(ItemEdit.get(), player, player::updateInventory);
        }
    }

    /**
     * Update InventoryView for player.<br><br>
     * In API versions 1.19.3 and earlier, there is no implicit consistency for inventory and
     * changes, so it has to be done manually (also Purpur has similar issue in later versions too).<br>
     * In API versions 1.19.4 and later, there is implicit consistency for inventory and changes.
     *
     * @param player The player which inventory view should be updated
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void updateViewDelayed(@NotNull Player player) {
        if (VersionUtility.isOlderEquals(1, 19, 4) || VersionUtility.hasPurpurAPI()) {
            SchedulerUtility.runLater(ItemEdit.get(), player, 1L, player::updateInventory);
        }
    }

    /**
     * @param player the player
     * @param item   item to be give, note: item.getAmount() is ignored
     * @param amount amount of item to be given
     * @param mode   how to handle special cases
     * @return the amount given (or given + dropped)
     */
    public static int giveAmount(@NotNull final HumanEntity player,
                                 @NotNull final ItemStack item,
                                 @Range(from = 0, to = Integer.MAX_VALUE) int amount,
                                 @NotNull final InventoryUtility.ExcessMode mode) {
        final ItemStack itemClone = item.clone();
        if (amount == 0)
            return 0;
        int remains = amount;
        while (remains > 0) {
            itemClone.setAmount(Math.min(itemClone.getMaxStackSize(), remains));
            HashMap<Integer, ItemStack> map = player.getInventory().addItem(itemClone);
            remains = remains - Math.min(itemClone.getMaxStackSize(), remains);
            if (map.isEmpty())
                continue;
            remains = remains + map.get(0).getAmount();
            break;
        }

        if (player instanceof Player)
            updateViewDelayed((Player) player);

        if (remains == 0)
            return amount;

        return switch (mode) {
            case DELETE_EXCESS -> amount - remains;
            case DROP_EXCESS -> {
                while (remains > 0) {
                    int drop = Math.min(remains, 64);
                    itemClone.setAmount(drop);
                    ItemStack itemCopy = new ItemStack(itemClone);
                    Location loc = player.getEyeLocation();
                    SchedulerUtility.run(ItemEdit.get(), loc,
                            () -> player.getWorld().dropItem(loc, itemCopy));
                    remains -= drop;
                }
                yield amount;
            }
            case CANCEL -> {
                removeAmount(player, itemClone, amount - remains, LackMode.REMOVE_MAX_POSSIBLE);
                yield 0;
            }
        };
    }

    /**
     * @param player the player
     * @param item   item to be give, note: item.getAmount() is ignored
     * @param amount amount of item to be given
     * @param mode   how to handle special cases
     * @return the removed amount
     */
    public static int removeAmount(@NotNull final HumanEntity player,
                                   @NotNull final ItemStack item,
                                   @Range(from = 0, to = Integer.MAX_VALUE) final int amount,
                                   @NotNull final InventoryUtility.LackMode mode) {
        final ItemStack itemClone = item.clone();
        if (amount == 0)
            return 0;
        if (player instanceof Player)
            updateViewDelayed((Player) player);

        return switch (mode) {
            case REMOVE_MAX_POSSIBLE -> {
                itemClone.setAmount(amount);
                HashMap<Integer, ItemStack> map = player.getInventory().removeItem(itemClone);

                if (map.isEmpty()) {
                    yield amount;
                }
                int left = map.get(0).getAmount();
                ItemStack[] extras = player.getInventory().getExtraContents();
                for (int i = 0; i < extras.length; i++) {
                    ItemStack extra = extras[i];
                    if (extra != null && itemClone.isSimilar(extra)) {
                        int toRemove = Math.min(left, extra.getAmount());
                        left -= toRemove;
                        if (toRemove == extra.getAmount())
                            extras[i] = null;
                        else {
                            extra.setAmount(extra.getAmount() - toRemove);
                            extras[i] = extra;
                        }
                    }
                }
                player.getInventory().setExtraContents(extras);
                yield amount - left;
            }
            case CANCEL -> {
                if (!player.getInventory().containsAtLeast(itemClone, amount)) {
                    yield 0;
                }

                itemClone.setAmount(amount);
                HashMap<Integer, ItemStack> map = player.getInventory().removeItem(itemClone);

                if (map.isEmpty()) {
                    yield amount;
                }
                yield amount - map.get(0).getAmount();
            }
        };
    }

    public enum ExcessMode {
        /**
         * drops if front of the player any items that can't be hold by the player
         */
        DROP_EXCESS,
        /**
         * remove any items that can't be hold by the player
         */
        DELETE_EXCESS,
        /**
         * if player has not enough space nothing is given to the player
         */
        CANCEL,
    }

    public enum LackMode {
        /**
         * remove the max number of items up to amount
         */
        REMOVE_MAX_POSSIBLE,
        /**
         * if there aren't enough items to remove, nothing is removed
         */
        CANCEL
    }
}
