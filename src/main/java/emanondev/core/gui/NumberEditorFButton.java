package emanondev.core.gui;

import emanondev.core.CoreMain;
import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import emanondev.core.message.DMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NumberEditorFButton<T extends Number> extends AGuiButton {

    private final T maxChangeAmount;
    private final T minChangeAmount;
    private final Supplier<T> getValue;
    private final Consumer<T> setValue;
    private final Supplier<ItemStack> baseItem;
    //private final Supplier<List<String>> baseDescription;
    //private final Supplier<List<String>> instructionsDescription;
    private T changeAmount;


    /**
     * @param gui              parent gui
     * @param changeAmountBase which amount should be the starting change value by
     *                         click
     * @param maxChangeValue   maximus allowed change amount by click amount
     * @param minChangeValue   minimum allowed change amount by click amount
     * @param getValue         get the value
     * @param setValue         set the value change
     * @param baseItem         display item
     * @param baseDescription  should contain value description
     * @throws IllegalArgumentException if maxChangeValue is less than
     *                                  minChangeValue
     * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
     *                                  or equals to 0
     */
    @Deprecated
    public NumberEditorFButton(@NotNull Gui gui,
                               @NotNull T changeAmountBase, @Nullable T minChangeValue, @Nullable T maxChangeValue,
                               @NotNull Supplier<T> getValue,
                               @NotNull Consumer<T> setValue,
                               @Nullable Supplier<ItemStack> baseItem,
                               @Nullable Supplier<List<String>> baseDescription) {
        this(gui, changeAmountBase, minChangeValue, maxChangeValue,
                getValue, setValue, baseItem, baseDescription, null);
    }

    /**
     * @param gui                     parent gui
     * @param changeAmountBase        which amount should be the starting change value by
     *                                click
     * @param maxChangeValue          maximus allowed change amount by click amount
     * @param minChangeValue          minimum allowed change amount by click amount
     * @param getValue                get the value
     * @param setValue                set the value change
     * @param baseItem                display item
     * @param baseDescription         should contain value description
     * @param instructionsDescription description of click effects
     * @throws IllegalArgumentException if maxChangeValue is less than
     *                                  minChangeValue
     * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
     *                                  or equals to 0
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public NumberEditorFButton(@NotNull Gui gui,
                               @NotNull T changeAmountBase, @Nullable T minChangeValue, @Nullable T maxChangeValue,
                               @NotNull Supplier<T> getValue,
                               @NotNull Consumer<T> setValue,
                               @Nullable Supplier<ItemStack> baseItem,
                               @Nullable Supplier<List<String>> baseDescription,
                               @Nullable Supplier<List<String>> instructionsDescription) {
        super(gui);
        this.changeAmount = changeAmountBase;
        if (maxChangeValue == null) {
            if (changeAmount instanceof BigDecimal)
                this.maxChangeAmount = (T) BigDecimal.valueOf(Double.MAX_VALUE);
            else if (changeAmount instanceof Double)
                this.maxChangeAmount = (T) (Double) Double.MAX_VALUE;
            else if (changeAmount instanceof Float)
                this.maxChangeAmount = (T) (Float) Float.MAX_VALUE;
            else if (changeAmount instanceof Integer)
                this.maxChangeAmount = (T) (Integer) Integer.MAX_VALUE;
            else if (changeAmount instanceof Long)
                this.maxChangeAmount = (T) (Long) Long.MAX_VALUE;
            else if (changeAmount instanceof Short)
                this.maxChangeAmount = (T) (Short) Short.MAX_VALUE;
            else
                throw new UnsupportedOperationException();
        } else
            this.maxChangeAmount = maxChangeValue;
        if (minChangeValue == null) {
            if (changeAmount instanceof BigDecimal)
                this.minChangeAmount = (T) BigDecimal.valueOf(Math.pow(10, -10));
            else if (changeAmount instanceof Double)
                this.minChangeAmount = (T) (Double) Math.pow(10, -10);
            else if (changeAmount instanceof Float)
                this.minChangeAmount = (T) (Float) (float) Math.pow(10, -10);
            else if (changeAmount instanceof Integer)
                this.minChangeAmount = (T) (Integer) 1;
            else if (changeAmount instanceof Long)
                this.minChangeAmount = (T) (Long) 1L;
            else if (changeAmount instanceof Short)
                this.minChangeAmount = (T) (Short) ((short) 1);
            else
                throw new UnsupportedOperationException();
        } else
            this.minChangeAmount = minChangeValue;
        checkBounds();
        this.getValue = getValue;
        this.setValue = setValue;
        this.baseItem = baseItem == null ? (
                () -> getDefaultBaseItem().setDescription(getDefaultDescription(gui.getTargetPlayer(), baseDescription, instructionsDescription)).build()) : (
                () -> new ItemBuilder(baseItem.get()).setDescription(getDefaultDescription(gui.getTargetPlayer(), baseDescription, instructionsDescription)).build());
    }


    public NumberEditorFButton(@NotNull Gui gui,
                               @NotNull T changeAmountBase, @Nullable T minChangeValue, @Nullable T maxChangeValue,
                               @NotNull Supplier<T> getValue,
                               @NotNull Consumer<T> setValue,
                               @NotNull Supplier<ItemStack> baseItem,
                               boolean addInstructions) {
        super(gui);
        this.changeAmount = changeAmountBase;
        if (maxChangeValue == null) {
            if (changeAmount instanceof BigDecimal)
                this.maxChangeAmount = (T) BigDecimal.valueOf(Double.MAX_VALUE);
            else if (changeAmount instanceof Double)
                this.maxChangeAmount = (T) (Double) Double.MAX_VALUE;
            else if (changeAmount instanceof Float)
                this.maxChangeAmount = (T) (Float) Float.MAX_VALUE;
            else if (changeAmount instanceof Integer)
                this.maxChangeAmount = (T) (Integer) Integer.MAX_VALUE;
            else if (changeAmount instanceof Long)
                this.maxChangeAmount = (T) (Long) Long.MAX_VALUE;
            else if (changeAmount instanceof Short)
                this.maxChangeAmount = (T) (Short) Short.MAX_VALUE;
            else
                throw new UnsupportedOperationException();
        } else
            this.maxChangeAmount = maxChangeValue;
        if (minChangeValue == null) {
            if (changeAmount instanceof BigDecimal)
                this.minChangeAmount = (T) BigDecimal.valueOf(Math.pow(10, -10));
            else if (changeAmount instanceof Double)
                this.minChangeAmount = (T) (Double) Math.pow(10, -10);
            else if (changeAmount instanceof Float)
                this.minChangeAmount = (T) (Float) (float) Math.pow(10, -10);
            else if (changeAmount instanceof Integer)
                this.minChangeAmount = (T) (Integer) 1;
            else if (changeAmount instanceof Long)
                this.minChangeAmount = (T) (Long) 1L;
            else if (changeAmount instanceof Short)
                this.minChangeAmount = (T) (Short) ((short) 1);
            else
                throw new UnsupportedOperationException();
        } else
            this.minChangeAmount = minChangeValue;
        checkBounds();
        this.getValue = getValue;
        this.setValue = setValue;
        this.baseItem = addInstructions ? (
                () -> new ItemBuilder(baseItem.get()).addDescription(new DMessage(CoreMain.get()).append(CoreMain.get().getLanguageConfig(getTargetPlayer())
                                .getStringList("gui_button.number_editor.instructions"), "%amount%",
                        UtilsString.formatOptional10Digit(changeAmount), "%amount_inc%",
                        UtilsString.formatOptional10Digit(multiplyEditor(10)), "%amount_dec%",
                        UtilsString.formatOptional10Digit(multiplyEditor(0.1)))).build()) : (
                () -> new ItemBuilder(baseItem.get()).applyPlaceholders(CoreMain.get(), null, "%value%",
                        UtilsString.formatOptional10Digit(getValue()), "%amount%",
                        UtilsString.formatOptional10Digit(changeAmount), "%amount_inc%",
                        UtilsString.formatOptional10Digit(multiplyEditor(10)), "%amount_dec%",
                        UtilsString.formatOptional10Digit(multiplyEditor(0.1))).build());
        //this.baseDescription = baseDescription;
        //this.instructionsDescription = instructionsDescription;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T subtractNumbers(T a, T b) {
        if (a instanceof BigDecimal && b instanceof BigDecimal)
            return (T) ((BigDecimal) a).subtract((BigDecimal) b);
        if (a instanceof Double && b instanceof Double)
            return (T) (Double) (a.doubleValue() - b.doubleValue());
        if (a instanceof Float && b instanceof Float)
            return (T) (Float) (a.floatValue() - b.floatValue());
        if (a instanceof Long && b instanceof Long)
            return (T) (Long) (a.longValue() - b.longValue());
        if (a instanceof Integer && b instanceof Integer)
            return (T) (Integer) (a.intValue() - b.intValue());
        if (a instanceof Short && b instanceof Short)
            return (T) (Short) (short) (a.shortValue() - b.shortValue());
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T addNumbers(T a, T b) {
        if (a instanceof BigDecimal && b instanceof BigDecimal)
            return (T) ((BigDecimal) a).add((BigDecimal) b);
        if (a instanceof Double && b instanceof Double)
            return (T) (Double) (a.doubleValue() + b.doubleValue());
        if (a instanceof Float && b instanceof Float)
            return (T) (Float) (a.floatValue() + b.floatValue());
        if (a instanceof Long && b instanceof Long)
            return (T) (Long) (a.longValue() + b.longValue());
        if (a instanceof Integer && b instanceof Integer)
            return (T) (Integer) (a.intValue() + b.intValue());
        if (a instanceof Short && b instanceof Short)
            return (T) (Short) (short) (a.shortValue() + b.shortValue());
        throw new UnsupportedOperationException();
    }

    private void checkBounds() {
        if (changeAmount instanceof BigDecimal) {
            if (((BigDecimal) minChangeAmount).compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException();
            if (((BigDecimal) maxChangeAmount).compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException();
            if (((BigDecimal) minChangeAmount).compareTo(((BigDecimal) maxChangeAmount)) > 0)
                throw new IllegalArgumentException();
            if (((BigDecimal) changeAmount).compareTo(((BigDecimal) maxChangeAmount)) > 0)
                changeAmount = maxChangeAmount;
            if (((BigDecimal) changeAmount).compareTo(((BigDecimal) minChangeAmount)) < 0)
                changeAmount = minChangeAmount;
            return;
        }
        if (changeAmount instanceof Double) {
            if ((Double) minChangeAmount <= 0 || (Double) maxChangeAmount <= 0)
                throw new IllegalArgumentException();
            if (((Double) minChangeAmount).compareTo(((Double) maxChangeAmount)) > 0)
                throw new IllegalArgumentException();
            if (changeAmount.doubleValue() > maxChangeAmount.doubleValue())
                changeAmount = maxChangeAmount;
            if (changeAmount.doubleValue() < minChangeAmount.doubleValue())
                changeAmount = minChangeAmount;
            return;
        }
        if (changeAmount instanceof Float) {
            if ((Float) minChangeAmount <= 0 || (Float) maxChangeAmount <= 0)
                throw new IllegalArgumentException();
            if (((Float) minChangeAmount).compareTo(((Float) maxChangeAmount)) > 0)
                throw new IllegalArgumentException();
            if (changeAmount.floatValue() > maxChangeAmount.floatValue())
                changeAmount = maxChangeAmount;
            if (changeAmount.floatValue() < minChangeAmount.floatValue())
                changeAmount = minChangeAmount;
            return;
        }
        if (changeAmount instanceof Integer) {
            if ((Integer) minChangeAmount <= 0 || (Integer) maxChangeAmount <= 0)
                throw new IllegalArgumentException();
            if (((Integer) minChangeAmount).compareTo(((Integer) maxChangeAmount)) > 0)
                throw new IllegalArgumentException();
            if (changeAmount.intValue() > maxChangeAmount.intValue())
                changeAmount = maxChangeAmount;
            if (changeAmount.intValue() < minChangeAmount.intValue())
                changeAmount = minChangeAmount;
            return;
        }
        if (changeAmount instanceof Long) {
            if ((Long) minChangeAmount <= 0 || (Long) maxChangeAmount <= 0)
                throw new IllegalArgumentException();
            if (((Long) minChangeAmount).compareTo(((Long) maxChangeAmount)) > 0)
                throw new IllegalArgumentException();
            if (changeAmount.longValue() > maxChangeAmount.longValue())
                changeAmount = maxChangeAmount;
            if (changeAmount.longValue() < minChangeAmount.longValue())
                changeAmount = minChangeAmount;
            return;
        }
        if (changeAmount instanceof Short) {
            if ((Short) minChangeAmount <= 0 || (Short) maxChangeAmount <= 0)
                throw new IllegalArgumentException();
            if (((Short) minChangeAmount).compareTo(((Short) maxChangeAmount)) > 0)
                throw new IllegalArgumentException();
            if (changeAmount.shortValue() > maxChangeAmount.shortValue())
                changeAmount = maxChangeAmount;
            if (changeAmount.shortValue() < minChangeAmount.shortValue())
                changeAmount = minChangeAmount;
            return;
        }
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT: {
                T old = getValue();
                setValue(subtractNumbers(old, getChangeAmount()));
                return !old.equals(getValue());
            }
            case RIGHT: {
                T old = getValue();
                setValue(addNumbers(old, getChangeAmount()));
                return !old.equals(getValue());
            }
            case SHIFT_RIGHT: {
                T old = getChangeAmount();
                setChangeAmount(multiplyEditor(10D));
                return !old.equals(getChangeAmount());
            }
            case SHIFT_LEFT: {
                T old = getChangeAmount();
                setChangeAmount(multiplyEditor(0.1D));
                return !old.equals(getChangeAmount());
            }
            case MIDDLE:
            default:
                return false;
        }
    }

    public T getValue() {
        return getValue.get();
    }

    public void setValue(T value) {
        setValue.accept(value);
    }

    public T getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(T changeAmount) {
        if (changeAmount.doubleValue() < 0)
            throw new IllegalArgumentException();
        this.changeAmount = changeAmount;
    }

    @SuppressWarnings("unchecked")
    private T multiplyEditor(double b) {
        if (this.changeAmount instanceof BigDecimal)
            return bound((T) ((BigDecimal) this.changeAmount).multiply(new BigDecimal(b)));
        if (this.changeAmount instanceof Double)
            return bound((T) (Double) (this.changeAmount.doubleValue() * b));
        if (this.changeAmount instanceof Float)
            return bound((T) (Float) (this.changeAmount.floatValue() * (float) b));
        if (this.changeAmount instanceof Long)
            return bound((T) (Long) Math.max(1L, (long) (this.changeAmount.doubleValue() * b)));
        if (this.changeAmount instanceof Integer)
            return bound((T) (Integer) Math.max(1, (int) (this.changeAmount.doubleValue() * b)));
        if (this.changeAmount instanceof Short)
            return bound((T) (Short) (short) Math.max(1, this.changeAmount.doubleValue() * b));
        throw new UnsupportedOperationException();
    }

    private T bound(T num) {
        if (num instanceof BigDecimal) {
            if (((BigDecimal) num).compareTo(((BigDecimal) maxChangeAmount)) > 0)
                num = maxChangeAmount;
            if (((BigDecimal) num).compareTo(((BigDecimal) minChangeAmount)) < 0)
                num = minChangeAmount;
            return num;
        }

        if (num instanceof Double) {
            if (num.doubleValue() > maxChangeAmount.doubleValue())
                num = maxChangeAmount;
            if (num.doubleValue() < minChangeAmount.doubleValue())
                num = minChangeAmount;
            return num;
        }

        if (num instanceof Float) {
            if (num.floatValue() > maxChangeAmount.floatValue())
                num = maxChangeAmount;
            if (num.floatValue() < minChangeAmount.floatValue())
                num = minChangeAmount;
            return num;
        }

        if (num instanceof Integer) {
            if (num.intValue() > maxChangeAmount.intValue())
                num = maxChangeAmount;
            if (num.intValue() < minChangeAmount.intValue())
                num = minChangeAmount;
            return num;
        }

        if (num instanceof Long) {
            if (num.longValue() > maxChangeAmount.longValue())
                num = maxChangeAmount;
            if (num.longValue() < minChangeAmount.longValue())
                num = minChangeAmount;
            return num;
        }

        if (num instanceof Short) {
            if (num.shortValue() > maxChangeAmount.shortValue())
                num = maxChangeAmount;
            if (num.shortValue() < minChangeAmount.shortValue())
                num = minChangeAmount;
            return num;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack getItem() {
        return baseItem.get();
        /*
        ItemStack base = getBaseItem();
        return base == null ? null
                : new ItemBuilder(base).setDescription(getDescription(), true, this.getTargetPlayer(), "%value%",
                UtilsString.formatOptional10Digit(getValue()), "%amount%",
                UtilsString.formatOptional10Digit(changeAmount), "%amount_inc%",
                UtilsString.formatOptional10Digit(multiplyEditor(10)), "%amount_dec%",
                UtilsString.formatOptional10Digit(multiplyEditor(0.1))).build();*/
    }

    @Deprecated
    public ItemStack getBaseItem() {
        return baseItem == null ? CoreMain.get().getConfig("guiConfig.yml")
                .loadGuiItem("number_editor", Material.REPEATER).build() : baseItem.get();
    }

    @Deprecated
    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        List<String> tmp = getBaseDescription();
        if (tmp != null)
            desc.addAll(getBaseDescription());
        tmp = getInstructionsDescription();
        if (tmp != null)
            desc.addAll(tmp);
        return desc;
    }

    @Deprecated
    public List<String> getBaseDescription() {
        return CoreMain.get().getLanguageConfig(getTargetPlayer())
                .getStringList("gui_button.number_editor.base");
    }

    @Deprecated
    public List<String> getInstructionsDescription() {
        return CoreMain.get().getLanguageConfig(getTargetPlayer())
                .getStringList("gui_button.number_editor.instructions");
    }


    private DMessage getDefaultDescription(Player player, Supplier<List<String>> base, Supplier<List<String>> instructions) {
        DMessage msg = new DMessage(CoreMain.get());
        String[] holders = new String[]{"%value%",
                UtilsString.formatOptional10Digit(getValue()), "%amount%",
                UtilsString.formatOptional10Digit(changeAmount), "%amount_inc%",
                UtilsString.formatOptional10Digit(multiplyEditor(10)), "%amount_dec%",
                UtilsString.formatOptional10Digit(multiplyEditor(0.1))};
        if (base == null)
            msg.append(CoreMain.get().getLanguageConfig(player)
                    .getStringList("gui_button.number_editor.base"), holders);
        else
            msg.append(base.get(), holders);
        msg.newLine();
        if (instructions == null)
            msg.append(CoreMain.get().getLanguageConfig(player)
                    .getStringList("gui_button.number_editor.instructions"), holders);
        else
            msg.append(instructions.get(), holders);
        return msg;
    }

    private ItemBuilder getDefaultBaseItem() {
        return CoreMain.get().getConfig("guiConfig.yml")
                .loadGuiItem("number_editor", Material.REPEATER);
    }

}
