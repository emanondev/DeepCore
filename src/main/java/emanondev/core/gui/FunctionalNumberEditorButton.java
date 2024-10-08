package emanondev.core.gui;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * @see emanondev.core.gui.NumberEditorFButton
 * @deprecated replaced
 */
public class FunctionalNumberEditorButton<T extends Number> extends AGuiButton {

    private final Consumer<T> changeRequest;
    private final Supplier<T> grabValue;
    private final T maxChangeAmount;
    private final T minChangeAmount;
    private final ItemStack base;
    private T changeAmount;
    private Function<T, List<String>> baseDescription = null;
    private BiFunction<T, T, List<String>> fullDescription = null;

    /**
     * @param gui              parent gui
     * @param grabValue        Supply the current value
     * @param changeRequest    Apply changes to current value
     * @param changeAmountBase Which should be the starting change value by click
     */
    public FunctionalNumberEditorButton(Gui gui, Supplier<T> grabValue, Consumer<T> changeRequest, T changeAmountBase) {
        this(gui, grabValue, changeRequest, (ItemStack) null, changeAmountBase);
    }

    /**
     * @param gui              parent gui
     * @param grabValue        Supply the current value
     * @param changeRequest    Apply changes to current value
     * @param base             Which item should be used for the button
     * @param changeAmountBase Which should be the starting change value by click
     */
    public FunctionalNumberEditorButton(Gui gui, Supplier<T> grabValue, Consumer<T> changeRequest, ItemStack base,
                                        T changeAmountBase) {
        this(gui, grabValue, changeRequest, base == null ? null : new ItemBuilder(base).setGuiProperty().build(),
                changeAmountBase, null, null);
    }

    /**
     * @param gui              parent gui
     * @param grabValue        Supply the current value
     * @param changeRequest    Apply changes to current value
     * @param base             Which item should be used for the button
     * @param changeAmountBase Which should be the starting change value by click
     * @param maxChangeValue   maximus allowed change amount by click amount
     * @param minChangeValue   minimum allowed change amount by click amount
     * @throws IllegalArgumentException if maxChangeValue is less than
     *                                  minChangeValue
     * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
     *                                  or equals to 0
     */
    @SuppressWarnings("unchecked")
    public FunctionalNumberEditorButton(Gui gui, Supplier<T> grabValue, Consumer<T> changeRequest, ItemStack base,
                                        T changeAmountBase, T minChangeValue, T maxChangeValue) {
        super(gui);
        if (changeRequest == null || grabValue == null || changeAmountBase == null)
            throw new NullPointerException();
        this.changeRequest = changeRequest;
        this.grabValue = grabValue;
        this.base = base == null ? new ItemBuilder(Material.REPEATER).setGuiProperty().build() : base;
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
    }

    /**
     * @param gui              parent gui
     * @param grabValue        Supply the current value
     * @param changeRequest    Apply changes to current value
     * @param base             Which material should be used for the button
     * @param changeAmountBase Which should be the starting change value by click
     */
    public FunctionalNumberEditorButton(Gui gui, Supplier<T> grabValue, Consumer<T> changeRequest, Material base,
                                        T changeAmountBase) {
        this(gui, grabValue, changeRequest, base == null ? null : new ItemBuilder(base).setGuiProperty().build(),
                changeAmountBase);
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
                changeRequest(addNumbers(old, getChangeAmount()));
                return !old.equals(getValue());
            }
            case RIGHT: {
                T old = getValue();
                changeRequest(subtractNumbers(old, getChangeAmount()));
                return !old.equals(getValue());
            }
            case SHIFT_LEFT: {
                T old = getChangeAmount();
                setChangeAmount(multiplyEditor(10D));
                return !old.equals(getChangeAmount());
            }
            case SHIFT_RIGHT: {
                T old = getChangeAmount();
                setChangeAmount(multiplyEditor(0.1D));
                return !old.equals(getChangeAmount());
            }
            case MIDDLE:
            default:
                return false;
        }
    }

    @Override
    public ItemStack getItem() {
        if (fullDescription != null)
            return new ItemBuilder(base).setDescription(fullDescription.apply(getValue(), changeAmount), true,
                    this.getTargetPlayer(), "%value%", String.valueOf(grabValue.get()), "%amount%", String.valueOf(changeAmount), "%amountx10%",
                    String.valueOf(multiplyEditor(10D)), "%amount/10%", String.valueOf(multiplyEditor(0.1))).build();
        if (baseDescription != null) {
            List<String> info = baseDescription.apply(getValue());
            if (info != null)
                info = new ArrayList<>(info);
            else
                info = new ArrayList<>();
            info.addAll(this.getLanguageSection(getTargetPlayer()).loadStringList("numberEditorInstructions",
                    Arrays.asList("&7[&fClick&7] &9Left&b/&9Right &7> &9+&b/&9- &e%amount%",
                            "&7[&fClick&7] &9Shift Left&b/&9Right &7> &e%amount% &9-> &e%amountx10%&b/&e%amount/10%")));
            return new ItemBuilder(base).setDescription(new ArrayList<>(info), true, this.getTargetPlayer(),
                    "%amount%", UtilsString.formatOptional10Digit(changeAmount),
                    "%amountx10%", UtilsString.formatOptional10Digit(multiplyEditor(10)),
                    "%amount/10%", UtilsString.formatOptional10Digit(multiplyEditor(0.1))).build();
        }
        List<String> info = new ArrayList<>(this.getLanguageSection(getTargetPlayer()).loadStringList("numberEditorFull",
                Arrays.asList("&6&lAmount: &e%value%", "", "&7[&fClick&7] &9Left&b/&9Right &7> &9+&b/&9- &e%amount%",
                        "&7[&fClick&7] &9Shift Left&b/&9Right &7> &9+&b/&9- &e%amount% &9-> &9+&b/&9- &e%amountx10%&b/&e%amount/10%")));
        return new ItemBuilder(base).setDescription(new ArrayList<>(info), true, this.getTargetPlayer(),
                "%value%", UtilsString.formatOptional10Digit(grabValue.get()),
                "%amount%", UtilsString.formatOptional10Digit(changeAmount),
                "%amountx10%", UtilsString.formatOptional10Digit(multiplyEditor(10)),
                "%amount/10%", UtilsString.formatOptional10Digit(multiplyEditor(0.1))).build();
    }

    public T getValue() {
        return grabValue.get();
    }

    public void changeRequest(T value) {
        changeRequest.accept(value);
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

    /**
     * holder %amount% for current value
     *
     * @param baseDescription
     * @return
     */
    public FunctionalNumberEditorButton<T> setBaseDescription(Function<T, List<String>> baseDescription) {
        this.baseDescription = baseDescription;
        return this;
    }

    public FunctionalNumberEditorButton<T> setFullDescription(BiFunction<T, T, List<String>> fullDescription) {
        this.fullDescription = fullDescription;
        return this;
    }

}
