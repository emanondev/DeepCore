package emanondev.core.gui;

import emanondev.core.CorePlugin;
import emanondev.core.PlayerSnapshot;
import emanondev.core.PlayerSnapshot.FieldType;
import emanondev.core.message.DMessage;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public abstract class AdvancedResearchGui<T> extends AnvilGui implements PagedGui {

    private static final int PLAYER_INVENTORY_SIZE = 36;
    private final GuiButton[] controlButtons = new GuiButton[9];
    private final NextPageButton nextB = new NextPageButton(this);
    private final PreviousPageButton prevB = new PreviousPageButton(this);
    private final BackButton backB = new BackButton(this);
    private final List<ContainerButton> buttons = new ArrayList<>();
    private final List<ContainerButton> activeButtons = new ArrayList<>();
    private final PlayerSnapshot snap = new PlayerSnapshot();
    private final ItemStack base;
    private int nextPageSlot = 8;
    private int previousPageSlot = 0;
    private int backGuiSlot = 4;
    private int page;
    private Predicate<T> show;
    private String lastText;

    /**
     * @param title          title of the gui
     * @param base           base item
     * @param player         target player
     * @param previousHolder previous gui
     * @param plugin         plugin of the gui
     */
    public AdvancedResearchGui(@Nullable DMessage title, Player player, @Nullable Gui previousHolder,
                               @NotNull CorePlugin plugin, ItemStack base) {
        super(title, player, previousHolder, plugin);
        this.page = Math.max(1, page);
        lastText = "";
        if (base == null || base.getType() == Material.AIR)
            base = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = base.getItemMeta();
        meta.setDisplayName(">");
        base.setItemMeta(meta);
        this.base = base;
        this.getInventory().setItem(0, base);
        this.setControlGuiButton(previousPageSlot, prevB);
        this.setControlGuiButton(backGuiSlot, backB);
        this.setControlGuiButton(nextPageSlot, nextB);
    }

    /**
     * @param title          title of the gui
     * @param base           base item
     * @param player         target player
     * @param previousHolder previous gui
     * @param plugin         plugin of the gui
     */
    public AdvancedResearchGui(@Nullable String title, ItemStack base, Player player, @Nullable Gui previousHolder,
                               @NotNull CorePlugin plugin) {
        this(title, base, player, previousHolder, plugin, false);
    }

    /**
     * @param title          title of the gui
     * @param base           base item
     * @param player         target player
     * @param previousHolder previous gui
     * @param plugin         plugin of the gui
     * @param isTimerUpdated update the gui every second?
     */
    public AdvancedResearchGui(@Nullable String title, ItemStack base, @Nullable Player player, @Nullable Gui previousHolder,
                               @NotNull CorePlugin plugin, boolean isTimerUpdated) {
        super(title, player, previousHolder, plugin, isTimerUpdated);
        this.page = Math.max(1, page);
        lastText = "";
        if (base == null || base.getType() == Material.AIR)
            base = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = base.getItemMeta();
        meta.setDisplayName(">");
        base.setItemMeta(meta);
        this.base = base;
        this.getInventory().setItem(0, base);
        this.setControlGuiButton(previousPageSlot, prevB);
        this.setControlGuiButton(backGuiSlot, backB);
        this.setControlGuiButton(nextPageSlot, nextB);
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
        InventoryView view = getView();
        if (view != null)
            view.getBottomInventory().setItem(slot, button == null ? null : button.getItem());
    }

    public InventoryView getView() {
        if (this.getInventory().getViewers().size() == 0)
            return null;
        HumanEntity viewer = this.getInventory().getViewers().get(0);
        if (!(viewer instanceof Player))
            return null;
        return viewer.getOpenInventory();
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

    public void updateControlButtons() {
        InventoryView view = getView();
        if (view == null)
            return;
        for (int i = 0; i < 9; i++)
            if (controlButtons[i] != null)
                view.getBottomInventory().setItem(i, controlButtons[i].getItem());
            else
                view.getBottomInventory().setItem(i, null);
    }

    /**
     * @param slot must be 0-8 value if button should be shown
     */
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

    /**
     * @param slot must be 0-8 value if button should be shown
     */
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

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        snap.apply(getTargetPlayer(), FieldType.INVENTORY);
    }

    /**
     * returns true if the page changed
     */
    @Override
    public boolean setPage(int pag) {
        pag = Math.min(Math.max(1, pag), getMaxPage());
        if (pag == page)
            return false;
        page = pag;
        reloadInventory();
        return true;
    }

    @Override
    public int getPage() {
        return page;
    }

    /**
     * starting from 1
     */
    public int getMaxPage() {
        InventoryView view = getView();
        return view == null ? 1 : (activeButtons.size() - 1) / (PLAYER_INVENTORY_SIZE - 9) + 1;
    }

    public void setShowPredicate(Predicate<T> showP) {
        this.show = showP;
        recalculateButtons();
    }

    private void recalculateButtons() {
        activeButtons.clear();
        if (show == null) {
            for (ContainerButton button : buttons)
                if (button.match(getRenameText()))
                    activeButtons.add(button);
        } else
            for (ContainerButton button : buttons)
                if (show.test(button.getValue()) && button.match(getRenameText()))
                    activeButtons.add(button);

        if (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0)
            reloadInventory();
        controlButtons[nextPageSlot] = nextB;
        controlButtons[previousPageSlot] = prevB;
        controlButtons[backGuiSlot] = backB;
    }

    private String getRenameText() {
        return lastText;
    }

    public void sort(Comparator<T> comparator) {
        buttons.sort((o1, o2) -> comparator.compare(o1.getValue(), o2.getValue()));
        recalculateButtons();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        InventoryView view = getView();
        if (view == null)
            return;
        if (event.getClickedInventory() != view.getBottomInventory())
            return;
        GuiButton b;
        if (event.getSlot() /* >= PLAYER_INVENTORY_SIZE - 9 */ < 9) {
            b = controlButtons[event.getSlot() /*- (PLAYER_INVENTORY_SIZE - 9)*/];
            if (b != null && b.onClick(event))
                reloadInventory();
            return;
        }
        int index = (PLAYER_INVENTORY_SIZE - 9) * (page - 1) + event.getSlot();
        if (activeButtons.size() <= index - 9)
            return;
        b = getButton(index - 9/**/);
        // activeButtons.get(index);

        if (b != null && b.onClick(event))
            updateInventory();
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (!event.getPlayer().equals(this.getTargetPlayer())) {
            event.setCancelled(true);
            return;
        }
        snap.loadFrom(getTargetPlayer(), FieldType.INVENTORY);
        this.reloadInventory();
    }

    /**
     * like updateInventory but also reload last line buttons
     */
    public void reloadInventory() {
        updateInventory();
        updateControlButtons();
    }

    @Override
    public void updateInventory() {
        InventoryView view = getView();
        if (view == null)
            return;
        for (int i = 0; i < PLAYER_INVENTORY_SIZE - 9; i++) {
            int slot = i + (page - 1) * (PLAYER_INVENTORY_SIZE - 9);
            view.getBottomInventory().setItem(i + 9, getButton(slot) == null ? null : getButton(slot).getItem());
        }
    }

    @Override
    public GuiButton getButton(int slot) {
        return activeButtons.size() > slot ? activeButtons.get(slot) : null;
    }

    @Override
    public void setButton(int slot, GuiButton button) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addButton(@NotNull GuiButton button) {
        throw new UnsupportedOperationException();
    }

    public GuiButton getControlButton(int slot) {
        if (slot < 0 || slot > 8)
            return null;
        return controlButtons[slot];
    }

    public void addElement(T value) {
        AdvancedResearchGui<T>.ContainerButton container = getContainer(value);
        buttons.add(container);
        if (show == null || show.test(value)) {
            activeButtons.add(container);
            if (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0)
                reloadInventory();
        }
    }

    private ContainerButton getContainer(T value) {
        return new ContainerButton(value);
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
            AdvancedResearchGui<T>.ContainerButton container = getContainer(value);
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
            AdvancedResearchGui<T>.ContainerButton container = getContainer(value);
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
                AdvancedResearchGui<T>.ContainerButton container = getContainer(value);
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
                AdvancedResearchGui<T>.ContainerButton container = getContainer(value);
                buttons.add(container);
                if (show == null || show.test(value)) {
                    activeButtons.add(container);
                    added = true;
                }
            }
        if (added && (!this.isUpdateOnOpen() || getInventory().getViewers().size() > 0))
            reloadInventory();
    }

    public void onTextChange(String newText) {
        if (newText == null || newText.isEmpty())
            this.getInventory().setItem(0, base);
        else if (newText.startsWith(">"))
            newText = newText.substring(1);
        if (lastText.equals(newText))
            return;
        lastText = newText;
        this.recalculateButtons();
        this.updateInventory();
    }

    public abstract boolean match(String text, T value);

    public abstract boolean onElementClick(InventoryClickEvent event, T value);

    public abstract ItemStack getElementItem(T value);

    private class ContainerButton implements GuiButton {

        private final T val;

        public ContainerButton(T value) {
            this.val = value;
        }

        public boolean match(String text) {
            return AdvancedResearchGui.this.match(text, getValue());
        }

        public T getValue() {
            return val;
        }

        @Override
        public boolean onClick(@NotNull InventoryClickEvent event) {
            return AdvancedResearchGui.this.onElementClick(event, getValue());
        }

        @Override
        public ItemStack getItem() {
            return AdvancedResearchGui.this.getElementItem(getValue());
        }

        public @NotNull Gui getGui() {
            return AdvancedResearchGui.this;
        }
    }
}