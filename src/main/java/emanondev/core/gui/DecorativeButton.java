package emanondev.core.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class DecorativeButton extends AGuiButton {

    private final ItemStack item;

    public DecorativeButton(Gui parent, ItemStack item) {
        super(parent);
        this.item = item;
    }

    @Override
    public boolean onDrag(InventoryDragEvent event) {
        return false;
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }


}
