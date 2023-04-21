package emanondev.core.gui;

import emanondev.core.CorePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfirmationGui extends ChestGui {

    /**
     * @param title          the title of the gui
     * @param player         target player
     * @param previousHolder may be null
     * @param plugin         plugin
     */
    public ConfirmationGui(@Nullable String title, @Nullable Player player, @Nullable Gui previousHolder,
                           @NotNull CorePlugin plugin) {
        super(title, 1, player, previousHolder, plugin);
        ItemStack item = getConfirmationItem(false);
        for (int i = 0; i < 3; i++)
            this.getInventory().setItem(i, item);
        item = getConfirmationItem(true);
        for (int i = 6; i < 9; i++)
            this.getInventory().setItem(i, item);
    }

    public abstract ItemStack getConfirmationItem(boolean value);

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (event.getSlot() >= 0 && event.getSlot() < 3)
            onConfirmation(false);
        else if (event.getSlot() >= 6 && event.getSlot() < 9)
            onConfirmation(true);
    }

    public abstract void onConfirmation(boolean value);

    @Override
    public void updateInventory() {
        ItemStack item = getConfirmationItem(false);
        for (int i = 0; i < 3; i++)
            this.getInventory().setItem(i, item);
        item = getConfirmationItem(true);
        for (int i = 6; i < 9; i++)
            this.getInventory().setItem(i, item);
    }

    @Override
    public GuiButton getButton(int slot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setButton(int slot, GuiButton button) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addButton(@NotNull GuiButton button) {
        throw new UnsupportedOperationException();
    }


}
