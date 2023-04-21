package emanondev.core.gui;

import emanondev.core.CoreMain;
import emanondev.core.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NextPageButton extends AGuiButton implements PagedButton {
    private final ItemBuilder item;

    public NextPageButton(PagedGui parent) {
        super(parent);
        this.item = CoreMain.get().getConfig("guiConfig.yml").loadGuiItem("next_page", Material.ARROW);
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        getGui().incPage();
        return false; //incPage should already call inventoryUpdate()
    }

    @Override
    public ItemStack getItem() {
        if (getPage() >= getGui().getMaxPage())
            return null;
        return item.setAmount(Math.min(getPage() + 1, 101)).setDescription(CoreMain.get().getLanguageConfig(getTargetPlayer())
                        .getStringList("gui_button.next_page")
                , true, getTargetPlayer(), "%target_page%", String.valueOf(getPage() + 1)).build();
    }

    @Override
    public @NotNull PagedGui getGui() {
        return (PagedGui) super.getGui();
    }

}