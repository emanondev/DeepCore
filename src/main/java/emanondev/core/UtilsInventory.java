package emanondev.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.List;

public final class UtilsInventory {

    private UtilsInventory() {
        throw new AssertionError();
    }

    public static boolean isSimilarIgnoreDamage(ItemStack item, ItemStack item2) {
        if (isAirOrNull(item))
            return isAirOrNull(item2);
        if (isAirOrNull(item2))
            return false;
        if (item.isSimilar(item2))
            return true;
        if (item.getType() != item2.getType())
            return false;
        ItemMeta meta1 = item.getItemMeta();
        if (!(meta1 instanceof Damageable))
            return false;
        ItemMeta meta2 = item2.getItemMeta();
        if (meta1.isUnbreakable() && meta2.isUnbreakable())
            return false;
        ((Damageable) meta2).setDamage(((Damageable) meta1).getDamage());
        return meta1.equals(meta2);
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    @Deprecated
    public static ItemStack getEquip(Player p, EquipmentSlot slot) {
        return p.getInventory().getItem(slot);
    }

    /**
     * @param player - the player
     * @param item   - item to be give, note: item.getAmount() is ignored
     * @param amount - amount of item to be given
     * @param mode   - how to handle special cases
     * @return the remaining amount given (or given + dropped)
     * @throws NullPointerException     if player is null
     * @throws NullPointerException     if item is null
     * @throws NullPointerException     if mode is null
     * @throws IllegalArgumentException if amount is negative
     */
    public static int giveAmount(@NotNull HumanEntity player, @NotNull ItemStack item, final int amount,
                                 @NotNull final ExcessManage mode) {
        if (amount < 0)
            throw new IllegalArgumentException("negative amount");
        item = item.clone();
        if (amount == 0)
            return 0;
        int remains = amount;
        while (remains > 0) {
            item.setAmount(Math.min(item.getMaxStackSize(), remains));
            HashMap<Integer, ItemStack> map = player.getInventory().addItem(item);
            remains = remains - Math.min(item.getMaxStackSize(), remains);
            if (map.isEmpty())
                continue;
            remains = remains + map.get(0).getAmount();
            break;
        }

        if (player instanceof Player)
            Bukkit.getScheduler().runTaskLater(CoreMain.get(), () -> ((Player) player).updateInventory(), 1L);

        if (remains == 0)
            return amount;

        switch (mode) {
            case DELETE_EXCESS:
                return amount - remains;
            case DROP_EXCESS:
                while (remains > 0) {
                    int drop = Math.min(remains, 64);
                    item.setAmount(drop);
                    player.getWorld().dropItem(player.getEyeLocation(), item);
                    remains -= drop;
                }
                return amount;
            case CANCEL:
                removeAmount(player, item, amount - remains, LackManage.REMOVE_MAX_POSSIBLE);
                return 0;
        }
        throw new IllegalArgumentException();
    }

    /**
     * @param player - the player
     * @param item   - item to be give, note: item.getAmount() is ignored
     * @param amount - amount of item to be given
     * @param mode   - how to handle special cases
     * @return the removed amount
     * @throws NullPointerException     if player is null
     * @throws NullPointerException     if item is null
     * @throws NullPointerException     if mode is null
     * @throws IllegalArgumentException if amount is negative
     */
    public static int removeAmount(@NotNull HumanEntity player, @NotNull ItemStack item, @Range(from = 0, to = Integer.MAX_VALUE) final int amount,
                                   @NotNull final LackManage mode) {
        item = item.clone();
        if (amount == 0)
            return 0;
        switch (mode) {
            case REMOVE_MAX_POSSIBLE -> {
                item.setAmount(amount);
                HashMap<Integer, ItemStack> map = player.getInventory().removeItem(item);

                if (player instanceof Player)
                    Bukkit.getScheduler().runTaskLater(CoreMain.get(), () -> ((Player) player).updateInventory(), 1L);

                if (map.isEmpty())
                    return amount;
                else
                    return amount - map.get(0).getAmount();

            }
            case CANCEL -> {
                if (player.getInventory().containsAtLeast(item, amount)) {
                    item.setAmount(amount);
                    HashMap<Integer, ItemStack> map = player.getInventory().removeItem(item);

                    if (player instanceof Player)
                        Bukkit.getScheduler().runTaskLater(CoreMain.get(), () -> ((Player) player).updateInventory(), 1L);

                    if (map.isEmpty())
                        return amount;
                    else
                        return amount - map.get(0).getAmount();
                }

                return 0;
            }
        }
        throw new IllegalArgumentException();
    }

    public static List<EquipmentSlot> getPlayerEquipmentSlots() {
        return List.of(EquipmentSlot.HAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFF_HAND);
    }

    public enum ExcessManage {
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

    public enum LackManage {
        /**
         * remove the max number of items up to amount
         */
        REMOVE_MAX_POSSIBLE,
        /**
         * if there aren't enough items to remove, nothing is removed
         */
        CANCEL,
    }

}
