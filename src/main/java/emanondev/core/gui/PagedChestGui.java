package emanondev.core.gui;

import emanondev.core.CorePlugin;
import emanondev.core.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Deprecated
public abstract class PagedChestGui extends ChestGui implements PagedGui {
    // private String rawTitle;

    private final GuiButton[] controlGuiButtons = new GuiButton[9];
    private final ArrayList<GuiButton> buttons = new ArrayList<>();
    private int page;

    public PagedChestGui(String title, int rows, Player p, Gui previousHolder, CorePlugin plugin, int page) {
        super(title, rows, p, previousHolder, plugin);
        if (rows == 1)
            throw new IllegalArgumentException("at least 2 rows");
        addStartButtons();
        this.page = page;

        // this.rawTitle=title;
        for (int i = 0; i < 9; i++)
            getInventory().setItem(getInventory().getSize() - 9 + i,
                    getPlugin().getConfig("guiconfig.yml").loadItemStack("default_spacer_item",
                            new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setGuiProperty().build()));

        if (this.loadPreviousPageButtonPosition() >= 0 && this.loadPreviousPageButtonPosition() < 9)
            setControlGuiButton(loadPreviousPageButtonPosition(), new PreviousPageButton(this));

        if (this.loadNextPageButtonPosition() >= 0 && this.loadNextPageButtonPosition() < 9)
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> setControlGuiButton(loadNextPageButtonPosition(), new NextPageButton(PagedChestGui.this)), 1);

        if (this.loadBackButtonPosition() >= 0 && this.loadBackButtonPosition() < 9)
            setControlGuiButton(loadBackButtonPosition(), new BackButton(this));
        updateInventory();
    }

    protected abstract void addStartButtons();

    /**
     * override to change position (default 2) last line slot 0 to 8 or anything
     * else if you won't show, warning this method is called in the constructor
     *
     * @return position
     */
    protected int loadPreviousPageButtonPosition() {
        return 2;
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
        controlGuiButtons[slot] = button;
        getInventory().setItem(getInventory().getSize() - 9 + slot, button.getItem());
    }

    /**
     * override to change position (default 6) last line slot 0 to 8 or anything
     * else if you won't show, warning this method is called in the constructor
     *
     * @return position
     */
    protected int loadNextPageButtonPosition() {
        return 6;
    }

    /**
     * override to change position (default 8) last line slot 0 to 8 or anything
     * else if you won't show, warning this method is called in the constructor
     *
     * @return position
     */
    protected int loadBackButtonPosition() {
        return 8;
    }

    /**
     * returns true if the page changed
     */
    public boolean setPage(int pag) {
        pag = Math.min(Math.max(1, pag), getMaxPage());
        if (pag == page)
            return false;
        page = pag;
        // TODO setInventory(getInventory(page));
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
        return (buttons.size() - 1) / (getInventory().getSize() - 9) + 1;
    }

    protected void reloadInventory() {
        updateInventory();
        for (int i = 0; i < 9; i++)
            if (controlGuiButtons[i] != null)
                getInventory().setItem(getInventory().getSize() - 9 + i, controlGuiButtons[i].getItem());
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() != getInventory())
            return;
        GuiButton b = getButton(event.getSlot());
        if (b != null)
            if (b.onClick(event))
                updateInventory();
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
    }

    @Override
    public void updateInventory() {
        for (int i = 0; i < this.getInventory().getSize() - 9; i++) {
            GuiButton b = getButton(i);
            if (b != null)
                this.getInventory().setItem(i, b.getItem());
            else
                this.getInventory().setItem(i, null);
        }

    }

    @Override
    public GuiButton getButton(int slot) {
        if (slot >= this.getInventory().getSize() - 9 && slot < this.getInventory().getSize()) {
            // controlbutton
            return controlGuiButtons[slot - (this.getInventory().getSize() - 9)];
        }
        int id = (page - 1) * (this.getInventory().getSize() - 9) + slot;
        if (id < 0 || id >= buttons.size())
            return null;
        return buttons.get(id);
    }

    @Override
    public void setButton(int slot, GuiButton button) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addButton(@NotNull GuiButton button) {
        buttons.add(button);
    }

}
