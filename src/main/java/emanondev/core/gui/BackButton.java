package emanondev.core.gui;

import emanondev.core.CoreMain;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BackButton extends AGuiButton {

    private final ItemStack item;

    public BackButton(Gui parent) {
        super(parent);
        if (parent.getPreviousGui() != null) {
            item = CoreMain.get().getConfig("guiConfig.yml").loadGuiItem("back", Material.DARK_OAK_DOOR)
                    .setDescription(CoreMain.get().getLanguageConfig(getTargetPlayer()).getStringList("gui_button.back"),
                            true, getTargetPlayer()).build();
            return;
        }
        item = CoreMain.get().getConfig("guiConfig.yml").loadGuiItem("close", Material.BARRIER)
                .setDescription(CoreMain.get().getLanguageConfig(getTargetPlayer()).getStringList("gui_button.close"),
                        true, getTargetPlayer()).build();
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        if (getGui().getPreviousGui() != null)
            getGui().getPreviousGui().open(event.getWhoClicked());
        else
            event.getWhoClicked().closeInventory();
        return false;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }
}