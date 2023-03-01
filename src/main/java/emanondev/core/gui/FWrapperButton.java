package emanondev.core.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class FWrapperButton extends AGuiButton {

    private final GuiButton button;
    private final Function<InventoryClickEvent, Boolean> onClick;
    private final Supplier<ItemStack> getItem;

    public FWrapperButton(@NotNull Gui parent, @NotNull GuiButton button,
                          @Nullable Function<InventoryClickEvent,Boolean> onClick,
                          @Nullable Supplier<ItemStack> getItem) {
        super(parent);
        this.button = button;
        this.onClick = onClick;
        this.getItem = getItem;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        return onClick==null?button.onClick(event):onClick.apply(event);
    }

    @Override
    public @Nullable ItemStack getItem() {
        return getItem==null?button.getItem():getItem.get();
    }
}
