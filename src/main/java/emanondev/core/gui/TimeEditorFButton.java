package emanondev.core.gui;

import emanondev.core.CoreMain;
import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TimeEditorFButton extends AGuiButton {

    private final Consumer<Long> changeRequest;
    private final Supplier<Long> grabValue;
    private final Long maxChangeAmount;
    private final Long minChangeAmount;
    private final Supplier<ItemStack> baseItem;
    private final long[] ranges = new long[]{1L, 10L, 60L, 600L, 3600L, 6 * 3600L, 24 * 3600L, 7 * 24 * 3600L, 4 * 7 * 24 * 3600L, 54 * 7 * 24 * 3600L};
    private Long changeAmount;
    private Supplier<List<String>> baseDescription = null;
    private Supplier<List<String>> fullDescription = null;


    /**
     * @param gui           parent gui
     * @param grabValue     Supply the current value (seconds)
     * @param changeRequest Apply changes to current value (seconds)
     * @param baseItem      Which item should be used for the button
     */
    public TimeEditorFButton(Gui gui, Supplier<Long> grabValue, Consumer<Long> changeRequest, @Nullable Supplier<ItemStack> baseItem) {
        this(gui, grabValue, changeRequest, baseItem,
                null, null, null);
    }

    /**
     * @param gui              parent gui
     * @param grabValue        Supply the current value (seconds)
     * @param changeRequest    Apply changes to current value (seconds)
     * @param baseItem         Which item should be used for the button
     * @param changeAmountBase Which should be the starting change value by click
     * @param maxChangeValue   maximus allowed change amount by click amount
     * @param minChangeValue   minimum allowed change amount by click amount
     * @throws IllegalArgumentException if maxChangeValue is less than
     *                                  minChangeValue
     * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
     *                                  or equals to 0
     */

    public TimeEditorFButton(Gui gui, @NotNull Supplier<Long> grabValue, @NotNull Consumer<Long> changeRequest, @Nullable Supplier<ItemStack> baseItem,
                             @Nullable Long changeAmountBase, @Nullable Long minChangeValue, @Nullable Long maxChangeValue) {
        super(gui);
        this.changeRequest = changeRequest;
        this.grabValue = grabValue;
        this.baseItem = baseItem;
        this.changeAmount = changeAmountBase == null ? 60L : changeAmountBase;
        this.maxChangeAmount = Objects.requireNonNullElse(maxChangeValue, Long.MAX_VALUE);
        this.minChangeAmount = Objects.requireNonNullElse(minChangeValue, 1L);
        checkBounds();
    }

    /**
     * @param gui              parent gui
     * @param grabValue        Supply the current value (seconds)
     * @param changeRequest    Apply changes to current value (seconds)
     * @param changeAmountBase Which should be the starting change value by click
     */
    public TimeEditorFButton(Gui gui, Supplier<Long> grabValue, Consumer<Long> changeRequest,
                             Long changeAmountBase) {
        this(gui, grabValue, changeRequest, null,
                changeAmountBase, null, null);
    }

    /**
     * @param gui              parent gui
     * @param grabValue        Supply the current value (seconds)
     * @param changeRequest    Apply changes to current value (seconds)
     * @param baseItem         Which item should be used for the button
     * @param changeAmountBase Which should be the starting change value by click
     */
    public TimeEditorFButton(Gui gui, Supplier<Long> grabValue, Consumer<Long> changeRequest, @Nullable Supplier<ItemStack> baseItem,
                             Long changeAmountBase) {
        this(gui, grabValue, changeRequest, baseItem,
                changeAmountBase, null, null);
    }

    private static Long addNumbers(Long a, Long b) {
        return a + b;
    }

    private static Long subtractNumbers(Long a, Long b) {
        return a - b;
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

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        switch (event.getClick()) {
            case RIGHT: {
                Long old = getValue();
                changeRequest(addNumbers(old, getChangeAmount()));
                return !old.equals(getValue());
            }
            case LEFT: {
                Long old = getValue();
                changeRequest(subtractNumbers(old, getChangeAmount()));
                return !old.equals(getValue());
            }
            case SHIFT_RIGHT: {
                Long old = getChangeAmount();
                setChangeAmount(multiply());
                return !old.equals(getChangeAmount());
            }
            case SHIFT_LEFT: {
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
        ItemStack base = getBaseItem();
        if (base == null)
            return null;

        if (fullDescription != null)
            return new ItemBuilder(base).setDescription(fullDescription.get(), true,
                    this.getTargetPlayer(), "%amount%", String.valueOf(getValue()), "%amount_inc%",
                    String.valueOf(multiply()), "%amount_dec%", String.valueOf(divide())).build();
        List<String> info = baseDescription != null ? baseDescription.get() :
                CoreMain.get().getLanguageConfig(getTargetPlayer()).getStringList("gui_button.time_editor.base");
        if (info != null)
            info = new ArrayList<>(info);
        else
            info = new ArrayList<>();

        info.addAll(CoreMain.get().getLanguageConfig(getTargetPlayer()).getStringList("gui_button.time_editor.instructions"));
        return new ItemBuilder(base).setDescription(info, true, this.getTargetPlayer(),
                "%amount%", UtilsString.getTimeStringSeconds(getTargetPlayer(), getValue()),
                "%amount%", UtilsString.getTimeStringSeconds(getTargetPlayer(), changeAmount),
                "%amount_inc%", UtilsString.getTimeStringSeconds(getTargetPlayer(), multiply()),
                "%amount_dec%", UtilsString.getTimeStringSeconds(getTargetPlayer(), divide())).build();
    }

    public ItemStack getBaseItem() {
        return baseItem == null ? CoreMain.get().getConfig("guiConfig.yml")
                .loadGuiItem("time_editor", Material.REPEATER).build() : baseItem.get();
    }

    public Long getValue() {
        return grabValue.get();
    }

    public void changeRequest(Long value) {
        changeRequest.accept(value);
    }

    public Long getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Long changeAmount) {
        if (changeAmount < 0)
            throw new IllegalArgumentException();
        this.changeAmount = changeAmount;
        checkBounds();
    }

    private Long multiply() {
        for (long range : ranges)
            if (range > this.changeAmount)
                return bound(range);
        return bound(ranges[ranges.length - 1]);
    }

    private Long divide() {
        for (int i = ranges.length - 1; i >= 0; i--)
            if (ranges[i] < this.changeAmount)
                return bound(ranges[i]);
        return bound(ranges[ranges.length - 1]);
    }

    private Long bound(Long num) {
        if (num > maxChangeAmount)
            num = maxChangeAmount;
        if (num < minChangeAmount)
            num = minChangeAmount;
        return num;

    }

    /**
     * holder %value% for current value
     *
     * @param baseDescription
     * @return this
     */
    public TimeEditorFButton setBaseDescription(Supplier<List<String>> baseDescription) {
        this.baseDescription = baseDescription;
        return this;
    }

    /**
     * holder %value% for current value, %amount% for amount editor, %amount_inc%, %amount_dec%
     *
     * @param fullDescription
     * @return this
     */
    public TimeEditorFButton setFullDescription(Supplier<List<String>> fullDescription) {
        this.fullDescription = fullDescription;
        return this;
    }

}
