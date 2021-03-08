package emanondev.core.gui;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.core.UtilsString;
import emanondev.core.ItemBuilder;

public class NextPageButton extends AGuiButton implements PagedButton {
	private ItemStack item;

	public NextPageButton(PagedGui parent) {
		super(parent);
		this.item = new ItemStack(getGui().getPlugin().getConfig("guiConfig.yml").getItemStack("next_page.item",
				new ItemBuilder(Material.ARROW).setGuiProperty().build()));
	}

	@Override
	public ItemStack getItem() {
		if (getGui().getMaxPage() <= getPage())
			return getGui().getPlugin().getConfig("guiConfig.yml").getItemStack("empty_slot.item",
					new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setGuiProperty().build());
		UtilsString.updateDescription(item,this.getMultiMessage(getTargetPlayer(),"next_page.description",
						Arrays.asList("&9Click to go to page &e%target_page%"), true,
				getTargetPlayer(), "%target_page%", String.valueOf(getGui().getPage() + 1)));
		return item;
	}

	@Override
	public boolean onDrag(InventoryDragEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		if (getGui().incPage())
			event.getWhoClicked().openInventory(getGui().getInventory());
		return false;
	}

	@Override
	public PagedGui getGui() {
		return (PagedGui) super.getGui();
	}

}