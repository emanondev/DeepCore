package emanondev.core.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;

public class TimeEditorButtonFunction extends AGuiButton {

	private final Consumer<Long> changeRequest;
	private final Supplier<Long> grabValue;
	private Long changeAmount;
	private final Long maxChangeAmount;
	private final Long minChangeAmount;
	private final ItemStack base;
	
	private final long[] ranges = new long[]{1L,10L,60L,600L,3600L,6*3600L,24*3600L,7*24*3600L,4*7*24*3600L,54*7*24*3600L};

	/**
	 * 
	 * @param gui              parent gui
	 * @param grabValue        Supply the current value
	 * @param changeRequest    Apply changes to current value
	 * @param changeAmountBase Which should be the starting change value by click
	 */
	public TimeEditorButtonFunction(Gui gui, Supplier<Long> grabValue, Consumer<Long> changeRequest, Long changeAmountBase) {
		this(gui, grabValue, changeRequest, (ItemStack) null, changeAmountBase);
	}

	/**
	 * 
	 * @param gui              parent gui
	 * @param grabValue        Supply the current value
	 * @param changeRequest    Apply changes to current value
	 * @param base             Which material should be used for the button
	 * @param changeAmountBase Which should be the starting change value by click
	 */
	public TimeEditorButtonFunction(Gui gui, Supplier<Long> grabValue, Consumer<Long> changeRequest, Material base,
			Long changeAmountBase) {
		this(gui, grabValue, changeRequest, base == null ? null : new ItemBuilder(base).setGuiProperty().build(),
				changeAmountBase);
	}

	/**
	 * 
	 * @param gui              parent gui
	 * @param grabValue        Supply the current value
	 * @param changeRequest    Apply changes to current value
	 * @param base             Which item should be used for the button
	 * @param changeAmountBase Which should be the starting change value by click
	 */
	public TimeEditorButtonFunction(Gui gui, Supplier<Long> grabValue, Consumer<Long> changeRequest, ItemStack base,
			Long changeAmountBase) {
		this(gui, grabValue, changeRequest, base == null ? null : new ItemBuilder(base).setGuiProperty().build(),
				changeAmountBase, null, null);
	}

	/**
	 * 
	 * @param gui              parent gui
	 * @param grabValue        Supply the current value
	 * @param changeRequest    Apply changes to current value
	 * @param base             Which item should be used for the button
	 * @param changeAmountBase Which should be the starting change value by click
	 * @param maxChangeValue   maximus allowed change amount by click amount
	 * @param minChangeValue   minimum allowed change amount by click amount
	 * @throws IllegalArgumentException if maxChangeValue is less than
	 *                                  minChangeValue
	 * @throws IllegalArgumentException if maxChangeValue or minChangeValue are less
	 *                                  or equals to 0
	 */
	
	public TimeEditorButtonFunction(Gui gui, Supplier<Long> grabValue, Consumer<Long> changeRequest, ItemStack base,
			Long changeAmountBase, Long minChangeValue, Long maxChangeValue) {
		super(gui);
		if (changeRequest == null || grabValue == null)
			throw new NullPointerException();
		this.changeRequest = changeRequest;
		this.grabValue = grabValue;
		this.base = base == null ? new ItemBuilder(Material.REPEATER).setGuiProperty().build() : base;
		this.changeAmount = changeAmountBase==null?60L:changeAmountBase;
		if (maxChangeValue == null) {
			this.maxChangeAmount = Long.MAX_VALUE;
		} else
			this.maxChangeAmount = maxChangeValue;
		if (minChangeValue == null) {
			this.minChangeAmount = 1L;
		} else
			this.minChangeAmount = minChangeValue;
		checkBounds();
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		switch (event.getClick()) {
		case LEFT: {
			Long old = getValue();
			changeRequest(addNumbers(old, getChangeAmount()));
			return !old.equals(getValue());
		}
		case RIGHT: {
			Long old = getValue();
			changeRequest(subtractNumbers(old, getChangeAmount()));
			return !old.equals(getValue());
		}
		case SHIFT_LEFT: {
			Long old = getChangeAmount();
			setChangeAmount(multiply());
			return !old.equals(getChangeAmount());
		}
		case SHIFT_RIGHT: {
			Long old = getChangeAmount();
			setChangeAmount(divide());
			return !old.equals(getChangeAmount());
		}
		case MIDDLE:
		default:
			return false;
		}
	}

	private final Function<Long, List<String>> baseDescription = null;
	private final BiFunction<Long, Long, List<String>> fullDescription = null;

	/**
	 * holder %amount% for current value
	 * 
	 * @param baseDescription
	 * @return
	 */
	public TimeEditorButtonFunction setBaseDescription(Function<Long, List<String>> baseDescription) {
		return this;
	}

	public TimeEditorButtonFunction setFullDescription(BiFunction<Long, Long, List<String>> fullDescription) {
		return this;
	}

	@Override
	public ItemStack getItem() {
		if (fullDescription != null)
			return new ItemBuilder(base).setDescription(fullDescription.apply(getValue(), changeAmount), true,
					this.getTargetPlayer(), "%amount%", String.valueOf(getValue()), "%amountx10%",
					String.valueOf(multiply()), "%amount/10%", String.valueOf(divide())).build();
		if (baseDescription != null) {
			List<String> info = baseDescription.apply(getValue());
			if (info != null)
				info = new ArrayList<>(info);
			else
				info = new ArrayList<>();
			info.addAll(this.getLanguageSection(getTargetPlayer()).loadStringList("timeEditorInstructions",
					Arrays.asList("&7[&fClick&7] &9Left&b/&9Right &7> &9+&b/&9- &e%amount%",
							"&7[&fClick&7] &9Shift Left&b/&9Right &7> &e%amount% &9-> &e%amountx10%&b/&e%amount/10%")));
			return new ItemBuilder(base).setDescription(new ArrayList<>(info), true, this.getTargetPlayer(),
					"%amount%", UtilsString.getTimeStringSeconds(getTargetPlayer(),changeAmount), 
					"%amountx10%", UtilsString.getTimeStringSeconds(getTargetPlayer(),multiply()), 
					"%amount/10%", UtilsString.getTimeStringSeconds(getTargetPlayer(),divide())).build();
		}
		List<String> info = new ArrayList<>(this.getLanguageSection(getTargetPlayer()).loadStringList("timeEditorFull",
				Arrays.asList("&6&lTime: &e%value%", "", "&7[&fClick&7] &9Left&b/&9Right &7> &9+&b/&9- &e%amount%",
						"&7[&fClick&7] &9Shift Left&b/&9Right &7> &9+&b/&9- &e%amount% &9-> &9+&b/&9- &e%amountx10%&b/&e%amount/10%")));
		return new ItemBuilder(base).setDescription(new ArrayList<>(info), true, this.getTargetPlayer(), 
				"%value%", UtilsString.getTimeStringSeconds(getTargetPlayer(),grabValue.get()),
				"%amount%", UtilsString.getTimeStringSeconds(getTargetPlayer(),changeAmount), 
				"%amountx10%", UtilsString.getTimeStringSeconds(getTargetPlayer(),multiply()), 
				"%amount/10%", UtilsString.getTimeStringSeconds(getTargetPlayer(),divide())).build();
	}

	public Long getChangeAmount() {
		return changeAmount;
	}

	public void setChangeAmount(Long changeAmount) {
		if (changeAmount.doubleValue() < 0)
			throw new IllegalArgumentException();
		this.changeAmount = changeAmount;
	}

	public void changeRequest(Long value) {
		changeRequest.accept(value);
	}

	public Long getValue() {
		return grabValue.get();
	}

	
	
	private static Long addNumbers(Long a, Long b) {
		return a + b;
	}

	
	private static Long subtractNumbers(Long a, Long b) {
		return a - b;
	}

	
	private Long multiply() {
		for (int i = 0; i<ranges.length;i++)
			if (ranges[i]>this.changeAmount)
				return bound(ranges[i]);
		return bound(ranges[ranges.length-1]);
	}
	
	private Long divide() {
		for (int i = ranges.length-1; i>=0;i--)
			if (ranges[i]<this.changeAmount)
				return bound(ranges[i]);
		return bound(ranges[ranges.length-1]);
	}

	private void checkBounds() {
		
			if (minChangeAmount <= 0 || maxChangeAmount <= 0)
				throw new IllegalArgumentException();
			if ((minChangeAmount).compareTo((maxChangeAmount)) > 0)
				throw new IllegalArgumentException();
			if (changeAmount > maxChangeAmount)
				changeAmount = maxChangeAmount;
			if (changeAmount < minChangeAmount)
				changeAmount = minChangeAmount;
		
	}

	private Long bound(Long num) {
		
			if (num > maxChangeAmount)
				num = maxChangeAmount;
			if (num < minChangeAmount)
				num = minChangeAmount;
			return num;
		
	}

}
