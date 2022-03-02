package emanondev.core.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import emanondev.core.CorePlugin;

public abstract class ConfirmationGui extends ChestGui {

	/**
	 * 
	 * @param title may be null
	 * @param p may be null
	 * @param previusHolder may be null
	 * @param plugin
	 */
	public ConfirmationGui(String title, Player p, Gui previusHolder,
			@NotNull CorePlugin plugin) {
		super(title, 1, p, previusHolder, plugin);
		ItemStack item = getConfirmationItem(false);
		for (int i =0 ;i<3;i++)
			this.getInventory().setItem(i, item);
		item = getConfirmationItem(true);
		for (int i =6 ;i<9;i++)
			this.getInventory().setItem(i, item);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if (event.getSlot()>=0 && event.getSlot()<3)
			onConfirmation(false);
		else if (event.getSlot()>=6 && event.getSlot()<9)
			onConfirmation(true);
	}

	public abstract void onConfirmation(boolean value);

	@Override
	public GuiButton getButton(int slot) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setButton(int slot, GuiButton button) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addButton(GuiButton button) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateInventory() {
		ItemStack item = getConfirmationItem(false);
		for (int i =0 ;i<3;i++)
			this.getInventory().setItem(i, item);
		item = getConfirmationItem(true);
		for (int i =6 ;i<9;i++)
			this.getInventory().setItem(i, item);
	}
	
	public abstract ItemStack getConfirmationItem(boolean value);
	
	
	
	

}
