package emanondev.core.gui;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import emanondev.core.CorePlugin;

public class MapGui extends ChestGui {

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

    public void putGuiButton(int pos, GuiButton button) {
        if (pos < 0)
            throw new IllegalArgumentException();
        if (button != null) {
            buttonsMap.put(pos, button);
            getInventory().setItem(pos, button.getItem());
        } else {
            buttonsMap.remove(pos);
            getInventory().setItem(pos, null);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
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
        putGuiButton(slot, button);
    }

    @Override
    public void addButton(GuiButton button) {
        if (button == null)
            throw new NullPointerException();
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