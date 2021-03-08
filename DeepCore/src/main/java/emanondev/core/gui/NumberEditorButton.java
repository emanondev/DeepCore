package emanondev.core.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.core.ItemBuilder;

public class NumberEditorButton<T extends Number> implements GuiButton {

	private final Consumer<T> changeRequest;
	private final Supplier<T> grabValue;
	private final Gui gui;
	private T changeAmount;
	private final ItemStack base;

	public NumberEditorButton(Gui gui,
			Supplier<T> grabValue, Consumer<T> changeRequest, T startAmount) {
		this(gui, grabValue, changeRequest, (ItemStack) null, startAmount);
	}
	public NumberEditorButton(Gui gui,
			Supplier<T> grabValue, Consumer<T> changeRequest,Material base, T startAmount) {
		this(gui, grabValue, changeRequest, base==null?null:new ItemBuilder(base).setGuiProperty().build(), startAmount);
	}
	
	public NumberEditorButton(Gui gui,
			Supplier<T> grabValue, Consumer<T> changeRequest,ItemStack base, T startAmount) {
		if (gui==null || changeRequest==null || grabValue==null)
			throw new NullPointerException();
		this.gui = gui;
		this.changeRequest = changeRequest;
		this.grabValue = grabValue;
		this.base = base == null?new ItemBuilder(Material.REPEATER).setGuiProperty().build():base;
		this.changeAmount = startAmount;
	}

	@Override
	public boolean onDrag(InventoryDragEvent event) {
		return false;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		switch (event.getClick()) {
		case LEFT:{
			T old = getValue();
			changeRequest((T) addNumbers(old,getChangeAmount()));
			return !old.equals(getValue());
		}
		case RIGHT:{
			T old = getValue();
			changeRequest((T) subtractNumbers(old,getChangeAmount()));
			return !old.equals(getValue());
		}
		case SHIFT_LEFT:{
			T old = getChangeAmount();
			setChangeAmount(multiplyNumbers(old,10D));
			return !old.equals(getChangeAmount());
		}
		case SHIFT_RIGHT:{
			T old = getChangeAmount();
			setChangeAmount(multiplyNumbers(old,0.1D));
			return !old.equals(getChangeAmount());
		}
		case MIDDLE:
		default:
			return false;
		}
	}
	private Function<T,List<String>> baseDescription = null;
	private BiFunction<T,T,List<String>> fullDescription = null;
	public NumberEditorButton<T> setBaseDecription(Function<T,List<String>> baseDescription){
		
		return this;
	}
	public NumberEditorButton<T> setFullDecription(BiFunction<T,T,List<String>> fullDescription){
		
		return this;
	}

	@Override
	public ItemStack getItem() {
		if (fullDescription!=null)
		return new ItemBuilder(base).setDescription(fullDescription.apply(getValue(), changeAmount), true, this.getTargetPlayer()).build();
		if (baseDescription!=null) {
			List<String> info = baseDescription.apply(getValue());
			if (info!=null)
				info = new ArrayList<>(info);
			else
				info = new ArrayList<>();
			info.add("&3Left Click &7to &fadd &e"+String.valueOf(changeAmount));
			info.add("&3Right Click &7to &fremove &e"+String.valueOf(changeAmount));
			info.add("&3Shift Left Click &eincrease &fedited amount");
			info.add("&3Shift Right Click &edecrease &fedited amount");
		return new ItemBuilder(base).setDescription(new ArrayList<>(info), true, this.getTargetPlayer()).build();
		}
		List<String> info = new ArrayList<>();
		info.add("&6&lAmount Editor");
		info.add("&9Amount: &e"+NumberEditorButton.this.getValue());
		info.add("");
		info.add("&3Left Click &7to &fadd &e"+String.valueOf(changeAmount));
		info.add("&3Right Click &7to &fremove &e"+String.valueOf(changeAmount));
		info.add("&3Shift Left Click &eincrease &fedited amount");
		info.add("&3Shift Right Click &edecrease &fedited amount");
		return new ItemBuilder(base).setDescription(new ArrayList<>(info), true, this.getTargetPlayer()).build();
	}
	
	public T getChangeAmount() {
		return changeAmount;
	}
	public void setChangeAmount(T changeAmount) {
		if (changeAmount.doubleValue()<0)
			throw new IllegalArgumentException();
		this.changeAmount = changeAmount;
	}
	
	@Override
	public Gui getGui() {
		return gui;
	}
	public void changeRequest(T value) {
		changeRequest.accept(value);
	}
	public T getValue() {
		return grabValue.get();
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Number> T addNumbers(T a, T b) {
	    if(a instanceof BigDecimal && b instanceof BigDecimal)
	        return (T)((BigDecimal) a).add((BigDecimal) b);
	    
	    if(a instanceof Double && b instanceof Double)
	        return (T)(Double) (a.doubleValue() + b.doubleValue());

	    if(a instanceof Float && b instanceof Float)
	        return (T)(Float) (a.floatValue() + b.floatValue());
	    
	    if(a instanceof Long && b instanceof Long)
	        return (T)(Long) (a.longValue() + b.longValue());
	    
	    if(a instanceof Integer && b instanceof Integer)
	        return (T)(Integer) (a.intValue() + b.intValue());
	    
	    if(a instanceof Short && b instanceof Short) 
	        return (T)(Short)(short) (a.shortValue() + b.shortValue());
	    throw new UnsupportedOperationException();
	}
	@SuppressWarnings("unchecked")
	private static <T extends Number> T subtractNumbers(T a, T b) {
	    if(a instanceof BigDecimal && b instanceof BigDecimal)
	        return (T)((BigDecimal) a).subtract((BigDecimal) b);
	    
	    if(a instanceof Double && b instanceof Double)
	        return (T)(Double) (a.doubleValue() - b.doubleValue());

	    if(a instanceof Float && b instanceof Float)
	        return (T)(Float) (a.floatValue() - b.floatValue());
	    
	    if(a instanceof Long && b instanceof Long)
	        return (T)(Long) (a.longValue() - b.longValue());
	    
	    if(a instanceof Integer && b instanceof Integer)
	        return (T)(Integer) (a.intValue() - b.intValue());
	    
	    if(a instanceof Short && b instanceof Short) 
	        return (T)(Short)(short) (a.shortValue() - b.shortValue());
	    throw new UnsupportedOperationException();
	}
	@SuppressWarnings("unchecked")
	private static <T extends Number> T multiplyNumbers(T a, double b) {
	    if(a instanceof BigDecimal)
	        return (T)((BigDecimal) a).multiply(new BigDecimal(b));
	    
	    if(a instanceof Double)
	        return (T)(Double) (a.doubleValue() * b);

	    if(a instanceof Float)
	        return (T)(Float) (a.floatValue() * (float) b);
	    
	    if(a instanceof Long)
	        return (T)(Long) Math.max(1L, (long) (a.doubleValue() * b));
	    
	    if(a instanceof Integer)
	        return (T)(Integer) Math.max(1, (int) (a.doubleValue() * b));
	    
	    if(a instanceof Short) 
	        return (T)(Short)(short) Math.max(1, a.doubleValue() * b);
	    throw new UnsupportedOperationException();
	}

}
