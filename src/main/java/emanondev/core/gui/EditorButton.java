package emanondev.core.gui;

import java.util.function.*;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;


@Deprecated
public class EditorButton<T> implements GuiButton {

	private Function<InventoryClickEvent,Boolean> onClick;
	private Supplier<ItemStack> item;
	private Consumer<T> changeRequest;
	private Supplier<T> grabValue;
	private final Gui gui;
	
	public EditorButton(Gui gui) {
		if (gui==null)
			throw new NullPointerException();
		this.gui = gui;
	}

	public EditorButton<T> setOnClick(Function<InventoryClickEvent,Boolean> onClick) {
		this.onClick = onClick;
		return this;
	}
	public EditorButton<T> setGetValue(Supplier<T> grabValue) {
		this.grabValue = grabValue;
		return this;
	}
	public EditorButton<T> setOnClick(Consumer<T> changeRequest) {
		this.changeRequest = changeRequest;
		return this;
	}
	public EditorButton<T> setGetItem(Supplier<ItemStack> item) {
		this.item = item;
		return this;
	}
	
	@Override
	public boolean onDrag(InventoryDragEvent event) {
		return false;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		if (onClick==null)
			return false;
		return onClick.apply(event);
	}
	
	public void changeRequest(T value) {
		if (changeRequest==null)
			throw new IllegalStateException();
		changeRequest.accept(value);
	}
	public T getValue() {
		if (grabValue==null)
			throw new IllegalStateException();
		return grabValue.get();
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
