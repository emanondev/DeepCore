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
    private final Function<InventoryClickEvent, Boolean> shouldOverrideOnClick;
    private final Supplier<Boolean> shouldOverrideGetItem;

    public FWrapperButton(@NotNull Gui parent, @NotNull GuiButton button,
                          @NotNull Function<InventoryClickEvent, Boolean> shouldOverrideOnClick,
                          @NotNull Supplier<Boolean> shouldOverrideGetItem,
                          @Nullable Function<InventoryClickEvent, Boolean> onClick,
                          @Nullable Supplier<ItemStack> getItem) {
        super(parent);
        this.button = button;
        this.shouldOverrideOnClick = shouldOverrideOnClick;
        this.shouldOverrideGetItem = shouldOverrideGetItem;
        this.onClick = onClick;
        this.getItem = getItem;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        return onClick == null || !shouldOverrideOnClick.apply(event)? button.onClick(event) : onClick.apply(event);
    }

    @Override
    public @Nullable ItemStack getItem() {
        return getItem == null || !shouldOverrideGetItem.get()? button.getItem() : getItem.get();
    }

    public boolean nestedOnClick(InventoryClickEvent event){
        return button.onClick(event);
    }

    public ItemStack nestedGetItem(){
        return button.getItem();
    }
}
