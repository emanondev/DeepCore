package emanondev.core.gui;

import java.util.*;
import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import emanondev.core.CorePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PagedListGui<T> extends ChestGui implements PagedGui {

    private int nextPageSlot = 8;
    private int previousPageSlot = 0;
    private int backGuiSlot = 4;

    private final NextPageButton nextB = new NextPageButton(this);
    private final PreviousPageButton prevB = new PreviousPageButton(this);
    private final BackButton backB = new BackButton(this);
    private int page;
    private final List<ContainerButton> buttons = new ArrayList<>();
    private final List<ContainerButton> activeButtons = new ArrayList<>();
    private Predicate<T> show;

    private void recalculateButtons() {
        activeButtons.clear();
        if (show == null)
            activeButtons.addAll(buttons);
        else
            for (int i = 0; i < buttons.size(); i++)
                if (show.test(buttons.get(i).getValue()))
                    activeButtons.add(buttons.get(i));
        if (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0)
            reloadInventory();
        controlButtons[nextPageSlot] = nextB;
        controlButtons[previousPageSlot] = prevB;
        controlButtons[backGuiSlot] = backB;
    }

    private final GuiButton[] controlButtons = new GuiButton[9];

    /**
     * This implementation uses the last row for pages buttons, those buttons won't
     * change position changing gui page
     *
     * @param title
     * @param rows
     * @param p
     * @param previousHolder
     * @param plugin
     * @param timerUpdate
     * @param page
     */
    public PagedListGui(String title, int rows, Player p, Gui previousHolder, CorePlugin plugin, boolean timerUpdate,
                        int page) {
        super(title, rows, p, previousHolder, plugin, timerUpdate);
        if (rows == 1)
            throw new IllegalArgumentException("at least 2 rows");
        this.page = Math.max(1, page);

    }

    /**
     * @param slot must be 0-8 value if button should be shown
     */
    public void setNextPageSlot(int slot) {
        if (slot == nextPageSlot)
            return;
        if (nextPageSlot >= 0 && nextPageSlot < 9)
            controlButtons[nextPageSlot] = null;
        nextPageSlot = slot;
        if (nextPageSlot >= 0 && nextPageSlot < 9)
            controlButtons[nextPageSlot] = nextB;
        if (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0)
            updateControlButtons();
    }

    public void setPreviousPageSlot(int slot) {
        if (slot == previousPageSlot)
            return;
        if (previousPageSlot >= 0 && previousPageSlot < 9)
            controlButtons[previousPageSlot] = null;
        previousPageSlot = slot;
        if (previousPageSlot >= 0 && previousPageSlot < 9)
            controlButtons[previousPageSlot] = prevB;
        if (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0)
            updateControlButtons();
    }

    public void setBackGuiSlot(int slot) {
        if (slot == backGuiSlot)
            return;
        if (backGuiSlot >= 0 && backGuiSlot < 9)
            controlButtons[backGuiSlot] = null;
        backGuiSlot = slot;
        if (backGuiSlot >= 0 && backGuiSlot < 9)
            controlButtons[backGuiSlot] = backB;
        if (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0)
            updateControlButtons();
    }

    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (isUpdateOnOpen())
            this.reloadInventory();
    }

    @Override
    public int getPage() {
        return page;
    }

    /**
     * returns true if the page changed
     */
    public boolean setPage(int pag) {
        pag = Math.min(Math.max(1, pag), getMaxPage());
        if (pag == page)
            return false;
        page = pag;
        reloadInventory();
        return true;
    }

    /**
     * like updateInventory but also reload last line buttons
     */
    public void reloadInventory() {
        updateInventory();
        updateControlButtons();
    }

    public void setShowPredicate(Predicate<T> showP) {
        this.show = showP;
        recalculateButtons();
    }

    public void sort(Comparator<T> comparator) {
        buttons.sort((o1, o2) -> comparator.compare(o1.getValue(), o2.getValue()));
        recalculateButtons();
    }

    /**
     * control buttons are the last line buttons
     *
     * @param slot   - from 0 to 8
     * @param button - what button? might be null
     */
    public void setControlGuiButton(int slot, GuiButton button) {
        if (slot < 0 || slot >= 9)
            return;
        controlButtons[slot] = button;
        getInventory().setItem(getInventory().getSize() - 9 + slot, button.getItem());
    }

    /**
     * starting from 1
     */
    public int getMaxPage() {
        return (activeButtons.size() - 1) / (getInventory().getSize() - 9) + 1;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() != getInventory())
            return;
        GuiButton b;
        if (event.getSlot() >= this.getInventory().getSize() - 9) {
            b = controlButtons[event.getSlot() - (this.getInventory().getSize() - 9)];
            if (b != null && b.onClick(event))
                reloadInventory();
            return;
        }
        int index = (this.getInventory().getSize() - 9) * (page - 1) + event.getSlot();
        b = getButton(index);//activeButtons.get(index);

        if (b != null && b.onClick(event))
            updateInventory();
    }

    @Override
    @Nullable
    public GuiButton getButton(int slot) {
        return activeButtons.size() > slot ? activeButtons.get(slot) : null;
    }

    public GuiButton getControlButton(int slot) {
        if (slot < 0 || slot > 8)
            return null;
        return controlButtons[slot];
    }

    @Override
    public void setButton(int slot, GuiButton button) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addButton(@NotNull GuiButton button) {
        throw new UnsupportedOperationException();
    }

    public void addElement(T value) {
        PagedListGui<T>.ContainerButton container = getContainer(value);
        buttons.add(container);
        if (show == null || show.test(value)) {
            activeButtons.add(container);
            if (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0)
                reloadInventory();
        }
    }

    public void clearElements() {
        buttons.clear();
        activeButtons.clear();
        if (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0)
            reloadInventory();
    }

    public void addElements(Collection<T> values) {
        boolean added = false;
        for (T value : values) {
            PagedListGui<T>.ContainerButton container = getContainer(value);
            buttons.add(container);
            if (show == null || show.test(value)) {
                activeButtons.add(container);
                added = true;
            }
        }
        if (added && (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0))
            reloadInventory();
    }

    public void addElements(T[] values) {
        boolean added = false;
        for (T value : values) {
            PagedListGui<T>.ContainerButton container = getContainer(value);
            buttons.add(container);
            if (show == null || show.test(value)) {
                activeButtons.add(container);
                added = true;
            }
        }
        if (added && (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0))
            reloadInventory();
    }

    public void addElements(Collection<T> values, Predicate<T> shouldAdd) {
        boolean added = false;
        for (T value : values)
            if (shouldAdd.test(value)) {
                PagedListGui<T>.ContainerButton container = getContainer(value);
                buttons.add(container);
                if (show == null || show.test(value)) {
                    activeButtons.add(container);
                    added = true;
                }
            }
        if (added && (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0))
            reloadInventory();
    }

    public void addElements(T[] values, Predicate<T> shouldAdd) {
        boolean added = false;
        for (T value : values)
            if (shouldAdd.test(value)) {
                PagedListGui<T>.ContainerButton container = getContainer(value);
                buttons.add(container);
                if (show == null || show.test(value)) {
                    activeButtons.add(container);
                    added = true;
                }
            }
        if (added && (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0))
            reloadInventory();
    }

    @Override
    public void updateInventory() {
        for (int i = 0; i < this.getInventory().getSize() - 9; i++) {
            int slot = i + (page - 1) * (this.getInventory().getSize() - 9);
            this.getInventory().setItem(i, getButton(slot) == null ? null : getButton(slot).getItem());
        }
    }

    public void updateControlButtons() {
        for (int i = 0; i < 9; i++)
            if (controlButtons[i] != null)
                getInventory().setItem(getInventory().getSize() - 9 + i, controlButtons[i].getItem());
            else
                getInventory().setItem(getInventory().getSize() - 9 + i, null);
    }

    public abstract ContainerButton getContainer(T value);

    public abstract class ContainerButton implements GuiButton {

        private final T val;

        public ContainerButton(T value) {
            this.val = value;
        }

        public T getValue() {
            return val;
        }

        public @NotNull Gui getGui() {
            return PagedListGui.this;
        }

    }

}
