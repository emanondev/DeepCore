package emanondev.core.gui;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;

public class BackButton extends AGuiButton {
	private ItemStack item;
	
	public BackButton(Gui parent) {
		super(parent);
		if (parent.getPreviusGui()!=null) {
			item = new ItemStack(getPlugin().getConfig("guiConfig.yml").loadItemStack("back.item",new ItemBuilder(Material.DARK_OAK_DOOR).setGuiProperty().build()));
			UtilsString.updateDescription(item,this.getMultiMessage(getTargetPlayer(),"back.description", Arrays.asList("&4&lGo Back"), true, getTargetPlayer()));
			return;
		}
		item = new ItemStack(getPlugin().getConfig("guiConfig.yml").loadItemStack("close.item",new ItemBuilder(Material.DARK_OAK_DOOR).setGuiProperty().build()));
		UtilsString.updateDescription(item,this.getMultiMessage(getTargetPlayer(),"close.description", Arrays.asList("&4&lClose"), true, getTargetPlayer()));
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	/*@Override
	public void onClick(Player clicker, ClickType click) {
		if (getGui().getPreviusGui()!=null) {
			getGui().getPreviusGui().updateInventory();
			clicker.openInventory(getGui().getPreviusGui().getInventory());
			return;
		}
		clicker.closeInventory();
	}*/

	@Override
	public boolean onDrag(InventoryDragEvent event) {
		return false;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		if (getGui().getPreviusGui()!=null) {
			getGui().getPreviusGui().updateInventory();
			event.getWhoClicked().openInventory(getGui().getPreviusGui().getInventory());
			return false;
		}
		event.getWhoClicked().closeInventory();
		return false;
	}
}