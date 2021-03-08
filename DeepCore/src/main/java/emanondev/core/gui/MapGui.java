package emanondev.core.gui;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import emanondev.core.CorePlugin;

public class MapGui extends ChestGui {

	public MapGui(String title, int rows, Player p, Gui previusHolder, CorePlugin plugin) {
		super(title, rows, p, previusHolder, plugin);
	}

	protected HashMap<Integer, GuiButton> buttonsMap = new HashMap<Integer, GuiButton>();

	public void putGuiButton(int pos, GuiButton button) {
		if (pos < 0)
			throw new IllegalArgumentException();
		if (button != null)
			buttonsMap.put(pos, button);
		else
			buttonsMap.remove(pos);

		if (button != null)
			getInventory().setItem(pos, button.getItem());
		else
			getInventory().setItem(pos, null);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if (event.getRawSlot() >= 0 && event.getRawSlot() < getInventory().getSize())
			if (buttonsMap.get(event.getRawSlot()) != null)
				if (buttonsMap.get(event.getRawSlot()).onClick(event))
					updateInventory();
	}

	@Override
	public void onDrag(InventoryDragEvent event) {
		// TODO
	}

	@Override
	public GuiButton getButton(int slot) {
		return buttonsMap.get(slot);
	}

	@Override
	public void setButton(int slot, GuiButton button) {
		putGuiButton(slot, button);
	}

	@Override
	public void addButton(GuiButton button) {
		for (int i = 0; i < getInventory().getSize(); i++)
			if (buttonsMap.get(i) == null) {
				putGuiButton(i, button);
				return;
			}
	}

	@Override
	public void updateInventory() {
		for (int i = 0; i < getInventory().getSize(); i++)
			if (buttonsMap.get(i) == null)
				getInventory().setItem(i, null);
			else
				getInventory().setItem(i, buttonsMap.get(i).getItem());
	}

	/*
	 * @Override public void onSlotClick(Player clicker, int slot, ClickType click)
	 * { if (slot>=0 && slot < getInventorySize()) if (buttonsMap.get(slot)!=null)
	 * buttonsMap.get(slot).onClick(clicker, click);
	 * 
	 * }
	 * 
	 * @Override public boolean updateInventory() { boolean result = false; for(int
	 * i = 0; i < getInventorySize(); i++) if (buttonsMap.get(i)!=null) if
	 * (buttonsMap.get(i).update()) { getInventory().setItem(i,
	 * buttonsMap.get(i).getItem()); result = true; } return result; }
	 */

}