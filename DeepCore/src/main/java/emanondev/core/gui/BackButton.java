package emanondev.core.gui;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;

public class BackButton extends AGuiButton {
	
	private final ItemStack item;
	
	public BackButton(Gui parent) {
		super(parent);
		if (parent.getPreviusGui()!=null) {
			item = new ItemStack(getPlugin().getConfig("guiConfig.yml").loadItemStack("back.item",new ItemBuilder(Material.DARK_OAK_DOOR).setGuiProperty().build()));
			UtilsString.updateDescription(item,getPlugin().getLanguageConfig(getTargetPlayer()).loadStringList("gui_button.back.description", Arrays.asList("&4&lGo Back")), getTargetPlayer(), true);
			return;
		}
		item = new ItemStack(getPlugin().getConfig("guiConfig.yml").loadItemStack("close.item",new ItemBuilder(Material.DARK_OAK_DOOR).setGuiProperty().build()));
		UtilsString.updateDescription(item,getPlugin().getLanguageConfig(getTargetPlayer()).loadStringList("gui_button.close.description", Arrays.asList("&4&lClose")), getTargetPlayer(), true);
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		if (getGui().getPreviusGui()!=null) {
			getGui().getPreviusGui().open(event.getWhoClicked());
			return false;
		}
		event.getWhoClicked().closeInventory();
		return false;
	}
}