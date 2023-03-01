package emanondev.core.gui;

import emanondev.core.CoreMain;
import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import emanondev.core.message.DMessage;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LongEditorFButton extends AGuiButton {

    private final boolean addInstructions;
    private long changeAmount;
    private final long maxChangeAmount;
    private final long minChangeAmount;
    private final Supplier<Long> getValue;
    private final Consumer<Long> setValue;
    private final Supplier<ItemStack> baseItem;

    /**
     * @param gui      parent gui
     * @param getValue get the value
     * @param setValue set the value change
     * @param baseItem display item
     * @throws IllegalArgumentException if maxChangeValue is less than
     *                                  minChangeValue
     * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
     *                                  or equals to 0
     */
    public LongEditorFButton(@NotNull Gui gui,
                             @NotNull Supplier<Long> getValue,
                             @NotNull Consumer<Long> setValue,
                             @Nullable Supplier<ItemStack> baseItem) {
        this(gui, 1, 1, 100000000,
                getValue, setValue, baseItem, true);
    }

    /**
     * @param gui              parent gui
     * @param changeAmountBase which amount should be the starting change value by
     *                         click
     * @param maxChangeValue   maximus allowed change amount by click amount
     * @param minChangeValue   minimum allowed change amount by click amount
     * @param getValue         get the value
     * @param setValue         set the value change
     * @param baseItem         display item
     * @throws IllegalArgumentException if maxChangeValue is less than
     *                                  minChangeValue
     * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
     *                                  or equals to 0
     */
    public LongEditorFButton(@NotNull Gui gui,
                             long changeAmountBase, long minChangeValue, long maxChangeValue,
                             @NotNull Supplier<Long> getValue,
                             @NotNull Consumer<Long> setValue,
                             @Nullable Supplier<ItemStack> baseItem) {
        this(gui, changeAmountBase, minChangeValue, maxChangeValue,
                getValue, setValue, baseItem, true);
    }

    /**
     * @param gui              parent gui
     * @param changeAmountBase which amount should be the starting change value by
     *                         click
     * @param maxChangeValue   maximus allowed change amount by click amount
     * @param minChangeValue   minimum allowed change amount by click amount
     * @param getValue         get the value
     * @param setValue         set the value change
     * @param baseItem         display item
     * @throws IllegalArgumentException if maxChangeValue is less than
     *                                  minChangeValue
     * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
     *                                  or equals to 0
     */
    public LongEditorFButton(@NotNull Gui gui,
                             long changeAmountBase, long minChangeValue, long maxChangeValue,
                             @NotNull Supplier<Long> getValue,
                             @NotNull Consumer<Long> setValue,
                             @Nullable Supplier<ItemStack> baseItem,
                             boolean addInstructions) {
        super(gui);
        this.changeAmount = changeAmountBase;
        this.maxChangeAmount = maxChangeValue;
        this.minChangeAmount = minChangeValue;
        checkBounds();
        this.getValue = getValue;
        this.setValue = setValue;
        this.baseItem = baseItem;
        this.addInstructions = addInstructions;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT: {
                long old = getValue();
                setValue(old - getChangeAmount());
                return old != getValue();
            }
            case RIGHT: {
                long old = getValue();
                setValue(old + getChangeAmount());
                return old != getValue();
            }
            case SHIFT_RIGHT: {
                long old = getChangeAmount();
                setChangeAmount(multiplyEditor(10D));
                return old != getChangeAmount();
            }
            case SHIFT_LEFT: {
                long old = getChangeAmount();
                setChangeAmount(multiplyEditor(0.1D));
                return old != getChangeAmount();
            }
            case MIDDLE:
            default:
                return false;
        }
    }

    public DMessage getInstructionsDescription() {
        return new DMessage(CoreMain.get(), getTargetPlayer()).appendLangList("gui_button.number_editor.instructions");
    }

    public long getValue() {
        return getValue.get();
    }

    public void setValue(long value) {
        setValue.accept(value);
    }

    public ItemStack getBaseItem() {
        return baseItem == null ? CoreMain.get().getConfig("guiConfig.yml")
                .loadGuiItem("number_editor", Material.REPEATER).build() : baseItem.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack base = getBaseItem();
        if (base == null)
            return null;
        ItemBuilder builder = new ItemBuilder(base);
        if (this.addInstructions)
            return builder.addDescription(getInstructionsDescription().applyHolders(getPlaceholders())).build();
        return builder.applyPlaceholders(getPlugin(), null, getPlaceholders()).build();
    }

    private String[] getPlaceholders() {
        return new String[]{"%value%", UtilsString.formatOptional10Digit(getValue()),
                "%amount%", UtilsString.formatOptional10Digit(changeAmount),
                "%amount_inc%", UtilsString.formatOptional10Digit(multiplyEditor(10)),
                "%amount_dec%", UtilsString.formatOptional10Digit(multiplyEditor(0.1))};
    }

    public long getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(long changeAmount) {
        if (changeAmount < 1)
            throw new IllegalArgumentException();
        this.changeAmount = changeAmount;
    }

    private long multiplyEditor(double b) {
        return bound((long) Math.max(1L, this.changeAmount * b));
    }

    private void checkBounds() {
        if (minChangeAmount <= 0 || minChangeAmount > maxChangeAmount)
            throw new IllegalArgumentException();
        if (changeAmount > maxChangeAmount)
            changeAmount = maxChangeAmount;
        if (changeAmount < minChangeAmount)
            changeAmount = minChangeAmount;

    }

    private long bound(long num) {
        if (num > maxChangeAmount)
            num = maxChangeAmount;
        if (num < minChangeAmount)
            num = minChangeAmount;
        return num;
    }

}
