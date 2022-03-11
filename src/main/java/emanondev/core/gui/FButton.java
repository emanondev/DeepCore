package emanondev.core.gui;

import java.util.function.*;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FButton extends AGuiButton {

    private final Function<InventoryClickEvent, Boolean> onClick;
    private final Supplier<ItemStack> getItem;

    /**
     * @param gui parent gui
     * @param getItem item supplier
     * @param onClick click handler
     */
    public FButton(Gui gui, @Nullable Supplier<ItemStack> getItem, @Nullable Function<InventoryClickEvent, Boolean> onClick) {
        super(gui);
        this.onClick = onClick;
        this.getItem = getItem;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        return onClick != null && onClick.apply(event);
    }

    @Override
    public ItemStack getItem() {
        return getItem == null ? null : getItem.get();
    }

}
