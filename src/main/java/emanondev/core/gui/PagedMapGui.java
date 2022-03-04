package emanondev.core.gui;

import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import emanondev.core.CorePlugin;

public class PagedMapGui extends ChestGui implements PagedGui {

    private int nextPageSlot = 8;
    private int previousPageSlot = 0;
    private int backGuiSlot = 4;

    private int page;
    private final HashMap<Integer, GuiButton> buttons = new HashMap<>();

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
     */
    public PagedMapGui(String title, int rows, Player p, Gui previousHolder, CorePlugin plugin) {
        this(title, rows, p, previousHolder, plugin, false, 0);
    }

    /**
     * This implementation uses the last row for pages buttons, those buttons won't
     * change position changing gui page
     *
     * @param title
     * @param rows
     * @param p
     * @param previousHolder
     * @param plugin
     * @param page
     */
    public PagedMapGui(String title, int rows, Player p, Gui previousHolder, CorePlugin plugin, int page) {
        this(title, rows, p, previousHolder, plugin, false, 0);
    }

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
     */
    public PagedMapGui(String title, int rows, Player p, Gui previousHolder, CorePlugin plugin, boolean timerUpdate) {
        this(title, rows, p, previousHolder, plugin, timerUpdate, 0);
    }

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
    public PagedMapGui(String title, int rows, Player p, Gui previousHolder, CorePlugin plugin, boolean timerUpdate,
                       int page) {
        super(title, rows, p, previousHolder, plugin, timerUpdate);
        if (rows == 1)
            throw new IllegalArgumentException("at least 2 rows");
        this.page = Math.max(1, page);
        this.setControlGuiButton(previousPageSlot, prevB);
        this.setControlGuiButton(backGuiSlot, backB);
        this.setControlGuiButton(nextPageSlot, nextB);
    }

    private final NextPageButton nextB = new NextPageButton(this);
    private final PreviousPageButton prevB = new PreviousPageButton(this);
    private final BackButton backB = new BackButton(this);

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
    }

    public void onOpen(InventoryOpenEvent event) {
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
        if (!this.isUpdateOnOpen() || !getInventory().getViewers().isEmpty())
            getInventory().setItem(getInventory().getSize() - 9 + slot, button.getItem());
    }

    /**
     * starting from 1
     */
    public int getMaxPage() {
        return buttons.isEmpty() ? 1 : (Collections.max(buttons.keySet()) - 1) / (getInventory().getSize() - 9) + 1;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != getInventory())
            return;
        GuiButton b;
        if (event.getSlot() >= this.getInventory().getSize() - 9) {
            b = controlButtons[event.getSlot() - (this.getInventory().getSize() - 9)];
            if (b != null && b.onClick(event))
                reloadInventory();
            return;
        }
        b = buttons.get((this.getInventory().getSize() - 9) * (page - 1) + event.getSlot());
        if (b != null)
            if (b.onClick(event))
                updateInventory();
    }

    @Override
    public GuiButton getButton(int slot) {
        return buttons.get(slot);
    }

    public GuiButton getControlButton(int slot) {
        if (slot < 0 || slot > 8)
            return null;
        return controlButtons[slot];
    }

    @Override
    public void setButton(int slot, GuiButton button) {
        if (button == null)
            buttons.remove(slot);
        else
            buttons.put(slot, button);
        if (!this.isUpdateOnOpen() || !getInventory().getViewers().isEmpty())
            updateInventory(); //TODO smarter check the update may be on another page so no need to update
    }

    @Override
    public void addButton(GuiButton button) {
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            if (buttons.containsKey(i))
                continue;
            setButton(i, button);
            return;
        }
        throw new UnsupportedOperationException();
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

}
