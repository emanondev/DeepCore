package emanondev.core.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class DecorativeButton extends AGuiButton {

    private final ItemStack item;

    public DecorativeButton(Gui parent, ItemStack item) {
        super(parent);
        this.item = item;
    }

    @Override
    public boolean onDrag(@NotNull InventoryDragEvent event) {
        return false;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }


}
