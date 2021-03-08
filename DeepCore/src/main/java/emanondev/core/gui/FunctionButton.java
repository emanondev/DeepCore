package emanondev.core.gui;

import java.util.function.*;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class FunctionButton implements GuiButton {
	
	private final Function<InventoryDragEvent,Boolean> onDrag;
	private final Function<InventoryClickEvent,Boolean> onClick;
	private final Supplier<ItemStack> item;
	private final Gui gui;
	
	public FunctionButton(Gui gui,Function<InventoryDragEvent,Boolean> onDrag,
			Function<InventoryClickEvent,Boolean> onClick,
			Supplier<ItemStack> item) {
		if (gui==null)
			throw new NullPointerException();
		this.onDrag = onDrag;
		this.onClick = onClick;
		this.item = item;
		this.gui = gui;
	}

	@Override
	public boolean onDrag(InventoryDragEvent event) {
		return onDrag==null?false:onDrag.apply(event);
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		return onClick==null?false:onClick.apply(event);
	}

	@Override
	public ItemStack getItem() {
		return item==null?null:item.get();
	}

	@Override
	public Gui getGui() {
		return gui;
	}

}
