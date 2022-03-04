package emanondev.core.gui;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BackButton extends AGuiButton {

    private final ItemStack item;

    public BackButton(Gui parent) {
        super(parent);
        if (parent.getPreviousGui() != null) {
            item = new ItemStack(getPlugin().getConfig("guiConfig.yml").loadItemStack("back.item", new ItemBuilder(Material.DARK_OAK_DOOR).setGuiProperty().build()));
            UtilsString.updateDescription(item, getPlugin().getLanguageConfig(getTargetPlayer()).loadStringList("gui_button.back.description", List.of("&4&lGo Back")), getTargetPlayer(), true);
            return;
        }
        item = new ItemStack(getPlugin().getConfig("guiConfig.yml").loadItemStack("close.item", new ItemBuilder(Material.DARK_OAK_DOOR).setGuiProperty().build()));
        UtilsString.updateDescription(item, getPlugin().getLanguageConfig(getTargetPlayer()).loadStringList("gui_button.close.description", List.of("&4&lClose")), getTargetPlayer(), true);
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        if (getGui().getPreviousGui() != null) {
            getGui().getPreviousGui().open(event.getWhoClicked());
            return false;
        }
        event.getWhoClicked().closeInventory();
        return false;
    }
}