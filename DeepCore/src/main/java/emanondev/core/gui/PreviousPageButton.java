package emanondev.core.gui;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;

public class PreviousPageButton extends AGuiButton implements PagedButton {
	private ItemStack item;

	public PreviousPageButton(PagedGui parent) {
		super(parent);
		this.item = new ItemStack(getPlugin().getConfig("guiConfig.yml").getItemStack("previous_page.item",
				new ItemBuilder(Material.ARROW).setGuiProperty().build()));
	}

	@Override
	public ItemStack getItem() {
		if (getPage()<=1)
			return null;
		UtilsString.updateDescription(item,
				getPlugin().getLanguageConfig(getTargetPlayer())
				.loadStringList("gui_button.previous_page.description", 
						Arrays.asList("&9Click to go to page &e%target_page%"))
				, getTargetPlayer(), true, "%target_page%", String.valueOf(getGui().getPage() - 1));
		return item;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		getGui().decPage();
		return false;
	}

	@Override
	public PagedGui getGui() {
		return (PagedGui) super.getGui();
	}

}