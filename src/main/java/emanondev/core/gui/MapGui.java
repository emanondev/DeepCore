package emanondev.core.gui;

import emanondev.core.CorePlugin;
import emanondev.core.message.DMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @see PagedMapGui
 */
public class MapGui extends ChestGui {

    /**
     * @param title
     * @param rows
     * @param p
     * @param previousHolder
     * @param plugin
     */
    public MapGui(DMessage title, int rows, Player p, Gui previousHolder, CorePlugin plugin) {
        super(title, rows, p, previousHolder, plugin);
    }

    /**
     * @param title
     * @param rows
     * @param p
     * @param previousHolder
     * @param plugin
     */
    public MapGui(String title, int rows, Player p, Gui previousHolder, CorePlugin plugin) {
        super(title, rows, p, previousHolder, plugin);
    }

    /**
     * @param title
     * @param rows
     * @param p
     * @param previousHolder
     * @param plugin
     * @param timerUpdate
     */
    public MapGui(String title, int rows, Player p, Gui previousHolder, CorePlugin plugin, boolean timerUpdate) {
        super(title, rows, p, previousHolder, plugin, timerUpdate);
    }

    /**
     * will replace with array GuiButton[rows*9]
     */
    @Deprecated
    protected HashMap<Integer, GuiButton> buttonsMap = new HashMap<>();

    /**
     * @see #setButton(int, GuiButton)
     */
    @Deprecated
    public void putGuiButton(int pos, GuiButton button) {
        setButton(pos, button);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (event.getRawSlot() >= 0 && event.getRawSlot() < getInventory().getSize())
            if (buttonsMap.get(event.getRawSlot()) != null)
                if (buttonsMap.get(event.getRawSlot()).onClick(event))
                    updateInventory();
    }

    @Override
    public GuiButton getButton(int slot) {
        return buttonsMap.get(slot);
    }

    @Override
    public void setButton(int slot, GuiButton button) {
        if (slot < 0)
            throw new IllegalArgumentException();
        if (slot >= getInventory().getSize())
            throw new IndexOutOfBoundsException("slot " + slot + " is outside gui slots");
        if (button != null) {
            buttonsMap.put(slot, button);
            getInventory().setItem(slot, button.getItem());
        } else {
            buttonsMap.remove(slot);
            getInventory().setItem(slot, null);
        }
    }

    @Override
    public void addButton(@NotNull GuiButton button) {
        for (int i = 0; i < getInventory().getSize(); i++)
            if (buttonsMap.get(i) == null) {
                putGuiButton(i, button);
                return;
            }
        throw new IndexOutOfBoundsException("Gui is full");
    }

    @Override
    public void updateInventory() {
        for (int i = 0; i < getInventory().getSize(); i++)
            getInventory().setItem(i, buttonsMap.get(i) == null ? null : buttonsMap.get(i).getItem());
    }

}