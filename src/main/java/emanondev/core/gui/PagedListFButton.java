package emanondev.core.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PagedListFButton<T extends Object> extends AGuiButton {

    private final PagedListFGui<T> pagedListFGui;
    private final Supplier<ItemStack> getItem;

    public PagedListFButton(@Nullable Gui gui, @NotNull Supplier<ItemStack> getItem, @NotNull String title, @Range(from = 2, to = 9) int rows, boolean timerUpdate,
                            @Nullable BiFunction<InventoryClickEvent, T, Boolean> onClickItem, @NotNull Function<T, ItemStack> getElementItem) {
        super(gui);
        this.getItem = getItem;
        pagedListFGui = new PagedListFGui<T>(title, rows, getGui().getTargetPlayer(), getGui(),
                getGui().getPlugin(), timerUpdate,
                onClickItem, getElementItem);
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        pagedListFGui.open(getTargetPlayer());
        return true;
    }

    @Override
    public @Nullable ItemStack getItem() {
        return getItem.get();
    }

    public void setNextPageSlot(int slot) {
        pagedListFGui.setNextPageSlot(slot);
    }

    public void setPreviousPageSlot(int slot) {
        pagedListFGui.setPreviousPageSlot(slot);
    }

    public void setBackGuiSlot(int slot) {
        pagedListFGui.setBackGuiSlot(slot);
    }

    public void setShowPredicate(Predicate<T> showP) {
        pagedListFGui.setShowPredicate(showP);
    }

    public void sort(@NotNull Comparator<T> comparator) {
        pagedListFGui.sort(comparator);
    }

    /**
     * control buttons are the last line buttons
     *
     * @param slot   - from 0 to 8
     * @param button - what button? might be null
     */
    public void setControlGuiButton(@Range(from = 0, to = 8) int slot, @Nullable GuiButton button) {
        pagedListFGui.setControlGuiButton(slot, button);
    }

    public void addElement(T value) {
        pagedListFGui.addElement(value);
    }

    public void clearElements() {
        pagedListFGui.clearElements();
    }

    public void addElements(Collection<T> values) {
        pagedListFGui.addElements(values);
    }

    public void addElements(T[] values) {
        pagedListFGui.addElements(values);
    }

    public void addElements(Collection<T> values, Predicate<T> shouldAdd) {
        pagedListFGui.addElements(values, shouldAdd);
    }

    public void addElements(T[] values, Predicate<T> shouldAdd) {
        pagedListFGui.addElements(values, shouldAdd);
    }
}
