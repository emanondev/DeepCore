package emanondev.core.gui;

import java.util.function.*;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class FButton extends AGuiButton {
	
	private final Function<InventoryClickEvent,Boolean> onClick;
	private final Supplier<ItemStack> getItem;

	/**
	 * 
	 * @param gui
	 * @param getItem may be null
	 * @param onClick may be null
	 */
	public FButton(Gui gui,Supplier<ItemStack> getItem,Function<InventoryClickEvent,Boolean> onClick) {
		super(gui);
		this.onClick = onClick;
		this.getItem = getItem;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		return onClick==null?false:onClick.apply(event);
	}

	@Override
	public ItemStack getItem() {
		return getItem==null?null:getItem.get();
	}

}
