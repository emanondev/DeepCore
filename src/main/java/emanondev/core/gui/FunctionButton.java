package emanondev.core.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see emanondev.core.gui.FButton
 * @deprecated replaced
 */
@Deprecated
public class FunctionButton extends AGuiButton {

    private final Function<InventoryDragEvent, Boolean> onDrag;
    private final Function<InventoryClickEvent, Boolean> onClick;
    private final Supplier<ItemStack> item;

    public FunctionButton(Gui gui, Function<InventoryDragEvent, Boolean> onDrag,
                          Function<InventoryClickEvent, Boolean> onClick,
                          Supplier<ItemStack> item) {
        super(gui);
        this.onDrag = onDrag;
        this.onClick = onClick;
        this.item = item;
    }

    @Override
    public boolean onDrag(@NotNull InventoryDragEvent event) {
        return onDrag != null && onDrag.apply(event);
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        return onClick != null && onClick.apply(event);
    }

    @Override
    public ItemStack getItem() {
        return item == null ? null : item.get();
    }

}
