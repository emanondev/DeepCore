package emanondev.core.gui;

import emanondev.core.CoreMain;
import emanondev.core.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PreviousPageButton extends AGuiButton implements PagedButton {
    private final ItemBuilder item;

    public PreviousPageButton(PagedGui parent) {
        super(parent);
        this.item = CoreMain.get().getConfig("guiConfig.yml").loadGuiItem("previous_page", Material.ARROW);
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        getGui().decPage();
        return false; //decPage should already call inventoryUpdate()
    }

    @Override
    public ItemStack getItem() {
        if (getPage() <= 1)
            return null;
        return item.setAmount(Math.min(getPage() - 1, 101)).setDescription(CoreMain.get().getLanguageConfig(getTargetPlayer())
                        .getStringList("gui_button.previous_page")
                , true, getTargetPlayer(), "%target_page%", String.valueOf(getPage() - 1)).build();
    }

    @Override
    public @NotNull PagedGui getGui() {
        return (PagedGui) super.getGui();
    }

}