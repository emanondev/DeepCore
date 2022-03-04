package emanondev.core.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;

public abstract class TimeEditorButton extends AGuiButton {

    private Long changeAmount;
    private final Long maxChangeAmount;
    private final Long minChangeAmount;
    private ItemStack base;

    public void setBaseItem(ItemStack baseItem) {
        base = baseItem;
    }

    private final long[] ranges = new long[]{1L, 10L, 60L, 600L, 3600L, 6 * 3600L, 24 * 3600L, 7 * 24 * 3600L,
            4 * 7 * 24 * 3600L, 54 * 7 * 24 * 3600L};

    /**
     * @param gui              parent gui
     * @param changeAmountBase Which should be the starting change value by click
     */
    public TimeEditorButton(Gui gui, Long changeAmountBase) {
        this(gui, null, changeAmountBase);
    }

    /**
     * @param gui              parent gui
     * @param base             Which item should be used for the button
     * @param changeAmountBase Which should be the starting change value by click
     */
    public TimeEditorButton(Gui gui, ItemStack base, Long changeAmountBase) {
        this(gui, base == null ? null : new ItemBuilder(base).setGuiProperty().build(), changeAmountBase, null, null);
    }

    /**
     * @param gui
     * @param changeAmountBase
     * @param minChangeValue
     * @param maxChangeValue
     */
    public TimeEditorButton(Gui gui, Long changeAmountBase, Long minChangeValue, Long maxChangeValue) {
        this(gui, null, changeAmountBase, null, null);
    }

    /**
     * @param gui              parent gui
     * @param base             Which item should be used for the button
     * @param changeAmountBase Which should be the starting change value by click
     * @param maxChangeValue   maximus allowed change amount by click amount
     * @param minChangeValue   minimum allowed change amount by click amount
     * @throws IllegalArgumentException if maxChangeValue is less than
     *                                  minChangeValue
     * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
     *                                  or equals to 0
     */

    public TimeEditorButton(Gui gui, ItemStack base, Long changeAmountBase, Long minChangeValue, Long maxChangeValue) {
        super(gui);
        this.base = base == null ? new ItemBuilder(Material.CLOCK).setGuiProperty().build() : base;
        this.changeAmount = changeAmountBase == null ? 60L : changeAmountBase;
        if (maxChangeValue == null) {
            this.maxChangeAmount = Long.MAX_VALUE;
        } else
            this.maxChangeAmount = maxChangeValue;
        if (minChangeValue == null) {
            this.minChangeAmount = 1L;
        } else
            this.minChangeAmount = minChangeValue;
        checkBounds();
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT: {
                Long old = getValue();
                onValueChange(addNumbers(old, getChangeAmount()));
                return !old.equals(getValue());
            }
            case RIGHT: {
                Long old = getValue();
                onValueChange(subtractNumbers(old, getChangeAmount()));
                return !old.equals(getValue());
            }
            case SHIFT_LEFT: {
                Long old = getChangeAmount();
                setChangeAmount(multiply());
                return !old.equals(getChangeAmount());
            }
            case SHIFT_RIGHT: {
                Long old = getChangeAmount();
                setChangeAmount(divide());
                return !old.equals(getChangeAmount());
            }
            case MIDDLE:
            default:
                return false;
        }
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(base).setDescription(getDescription(), true, this.getTargetPlayer(), "%value%",
                UtilsString.getTimeStringSeconds(getTargetPlayer(), getValue()), "%amount%",
                UtilsString.getTimeStringSeconds(getTargetPlayer(), changeAmount), "%amountx10%",
                UtilsString.getTimeStringSeconds(getTargetPlayer(), multiply()), "%amount/10%",
                UtilsString.getTimeStringSeconds(getTargetPlayer(), divide())).build();
    }

    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.addAll(getBaseDescription());
        list.addAll(getInstructionsDescription());
        return list;
    }

    public List<String> getBaseDescription() {
        return Arrays.asList("&6&lTime: &e%value%", "");
    }

    public List<String> getInstructionsDescription() {
        return Arrays.asList("&7[&fClick&7] &9Left&b/&9Right &7> &9+&b/&9- &e%amount%",
                "&7[&fClick&7] &9Shift Left&b/&9Right &7> &9+&b/&9- &e%amount% &9-> &9+&b/&9- &e%amountx10%&b/&e%amount/10%");
    }

    public Long getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Long changeAmount) {
        if (changeAmount.doubleValue() < 0)
            throw new IllegalArgumentException();
        this.changeAmount = changeAmount;
    }

    public abstract void onValueChange(Long value);

    public abstract Long getValue();

    private static Long addNumbers(Long a, Long b) {
        return a + b;
    }

    private static Long subtractNumbers(Long a, Long b) {
        return a - b;
    }

    private Long multiply() {
        for (int i = 0; i < ranges.length; i++)
            if (ranges[i] > this.changeAmount)
                return bound(ranges[i]);
        return bound(ranges[ranges.length - 1]);
    }

    private Long divide() {
        for (int i = ranges.length - 1; i >= 0; i--)
            if (ranges[i] < this.changeAmount)
                return bound(ranges[i]);
        return bound(ranges[ranges.length - 1]);
    }

    private void checkBounds() {

        if (minChangeAmount <= 0 || maxChangeAmount <= 0)
            throw new IllegalArgumentException();
        if ((minChangeAmount).compareTo((maxChangeAmount)) > 0)
            throw new IllegalArgumentException();
        if (changeAmount > maxChangeAmount)
            changeAmount = maxChangeAmount;
        if (changeAmount < minChangeAmount)
            changeAmount = minChangeAmount;

    }

    private Long bound(Long num) {

        if (num > maxChangeAmount)
            num = maxChangeAmount;
        if (num < minChangeAmount)
            num = minChangeAmount;
        return num;

    }

}
