package emanondev.core.gui;

import emanondev.core.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemEditorFButton extends AGuiButton {

    private final Supplier<ItemStack> getValue;
    private final Supplier<ItemStack> getItem;
    private final Consumer<ItemStack> setValue;
    private final Consumer<InventoryCloseEvent> onClose;

    public ItemEditorFButton(Gui parent, Supplier<ItemStack> getItem, Supplier<ItemStack> getValue,
                             Consumer<ItemStack> setValue, Consumer<InventoryCloseEvent> onClose) {
        super(parent);
        if (getValue == null || getItem == null || setValue == null)
            throw new NullPointerException();
        this.getValue = getValue;
        this.getItem = getItem;
        this.setValue = setValue;
        this.onClose = onClose;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT)
            return false;
        new ItemGui().open(getTargetPlayer());
        return false;
    }

    @Override
    public ItemStack getItem() {
        return getItem.get();
    }

    public ItemStack getValue() {
        return getValue.get();
    }

    public void setValue(ItemStack item) {
        setValue.accept(item);
    }

    private class ItemGui extends MapGui {
        public ItemGui() {
            super("&9Item Selector", 1, ItemEditorFButton.this.getTargetPlayer(), ItemEditorFButton.this.getGui(),
                    ItemEditorFButton.this.getGui().getPlugin());
            this.setButton(8, new BackButton(this));
            this.setButton(4, new FButton(this, () -> new ItemBuilder(Material.PAPER)
                    .setDescription(Arrays.asList("&6Instructions",
                            "&9Click on the item of your inventory", "&9that you wish to use"))
                    .build(), (e) -> false));
        }

        public void onClick(@NotNull InventoryClickEvent event) {
            if (event.getClickedInventory() == this.getInventory()) {
                super.onClick(event);
                return;
            }
            setValue(event.getCurrentItem() == null || event.getCurrentItem().getType().isAir() ? null : new ItemStack(event.getCurrentItem()));
            ItemEditorFButton.this.getGui().open(getTargetPlayer());
        }

        @Override
        public void onClose(@NotNull InventoryCloseEvent event) {
            onClose.accept(event);
        }
    }

}
