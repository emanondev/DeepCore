package emanondev.core.gui;

import java.util.*;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;

public class StringListEditorFButton extends AGuiButton {

	private Action action = Action.ADD_LINE;
	private int line;
	private String subTitle = "&9Line editor";
	private Supplier<List<String>> getValue;
	private Consumer<List<String>> setValue;
	private Supplier<ItemStack> baseItem;
	private Supplier<List<String>> baseDescription;
	private Supplier<List<String>> valueDescription;
	private Supplier<List<String>> instructionsDescription;

	/**
	 * 
	 * @param parent
	 * @param getValue
	 * @param setValue
	 * @param baseItem may be null
	 * @param baseDescription may be null
	 * @param valueDescription may be null
	 * @param instructionsDescription may be null
	 */
	public StringListEditorFButton(Gui parent, @NotNull Supplier<List<String>> getValue,
			@NotNull Consumer<List<String>> setValue, Supplier<ItemStack> baseItem,
			Supplier<List<String>> baseDescription, Supplier<List<String>> valueDescription,
			Supplier<List<String>> instructionsDescription) {
		super(parent);
		if (getValue == null || setValue == null)
			throw new NullPointerException();
		this.getValue = getValue;
		this.setValue = setValue;
		this.baseItem = baseItem;
		this.baseDescription = baseDescription;
		this.valueDescription = valueDescription;
		this.instructionsDescription = instructionsDescription;
	}

	public void setLineEditorTitle(String value) {
		subTitle = value;
	}

	public List<String> getValue() {
		return getValue.get();
	}

	public void setValue(List<String> list) {
		setValue.accept(list);
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		switch (event.getClick()) {
		case LEFT:
			setLine(getLine() - 1);
			return true;
		case RIGHT:
			setLine(getLine() + 1);
			return true;
		case MIDDLE:
			applyAction();
			return true;
		case SHIFT_LEFT:
			action = Action.values()[(action.ordinal() - 1 + Action.values().length) % Action.values().length];
			return true;
		case SHIFT_RIGHT:
			action = Action.values()[(action.ordinal() + 1 + Action.values().length) % Action.values().length];
			return true;
		default:
			break;
		}
		return false;
	}

	private void setLine(int value) {
		line = value;
	}

	private int getLine() {
		List<String> value = getValue();
		if (value == null) {
			action = Action.ADD_LINE;
			return 0;
		}
		int r = Math.min(value.size(), Math.max(0, line));
		if (r == value.size())
			action = Action.ADD_LINE;
		if (r == 0 && action == Action.MOVE_UP)
			action = Action.MOVE_DOWN;
		return r;
	}

	private void applyAction() {
		List<String> list = getValue();
		if (list == null)
			list = new ArrayList<>();
		else
			list = new ArrayList<>(list);
		int line = getLine();
		switch (action) {
		case ADD_LINE:
			try {
				if (line >= list.size())
					list.add("&f");
				else
					list.add(line, "&f");
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.setValue(list);
			break;
		case DELETE_LINE:
			try {
				list.remove(line);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.setValue(list);
			break;
		case EDIT_LINE:
			final List<String> temp = list;
			new TextEditorGui(subTitle, list.get(line), getTargetPlayer(), this.getGui(), getPlugin()) {

				@Override
				public void onTextConfirmed(String lineText) {
					temp.set(line, lineText);
					StringListEditorFButton.this.setValue(temp);
					StringListEditorFButton.this.getGui().open(getTargetPlayer());
				}

			}.open(getTargetPlayer());
			break;
		case MOVE_DOWN:
			try {
				if (line + 1 >= list.size())
					list.add(line, "");
				list.add(line, list.remove(line));
				setLine(line + 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.setValue(list);
			break;
		case MOVE_UP:
			try {
				if (line - 1 < 0) {
					action = Action.MOVE_DOWN;
					getGui().updateInventory();
					return;
				}
				list.add(line - 1, list.remove(line));
				setLine(line - 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.setValue(list);
			break;
		default:
			throw new IllegalStateException();
		}

	}

	public ItemStack getBaseItem() {
		return baseItem == null ? new ItemBuilder(Material.PAPER).setGuiProperty().build() : baseItem.get();
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = getBaseItem();
		if (item == null)
			return null;
		return new ItemBuilder(item).setDescription(getDescription()).build();
	}

	public List<String> getDescription() {
		List<String> desc = new ArrayList<>();
		List<String> tmp = getBaseDescription();
		if (tmp != null)
			desc.addAll(getBaseDescription());
		tmp = getValueDescription();
		if (tmp != null)
			desc.addAll(tmp);
		tmp = getInstructionsDescription();
		if (tmp != null)
			desc.addAll(tmp);
		return UtilsString.fix(desc, null, true, "%action_symbol%", action.symbol, "%action_description%", action.desc,
				"%action_line%", actionLine());
	}

	public List<String> getBaseDescription() {
		return baseDescription == null ? Arrays.asList("&6&lText:") : baseDescription.get();
	}

	public List<String> getValueDescription() {
		if (valueDescription != null)
			return valueDescription.get();
		List<String> val = getValue();
		if (val == null)
			val = new ArrayList<>();
		else
			val = new ArrayList<>(val);
		val.add("");
		int line = getLine();
		for (int i = 0; i < val.size(); i++) {
			if (i != line)
				val.set(i, "   &f" + val.get(i));
			else
				val.set(i, "%action_symbol% &f" + val.get(i));
		}
		return val;
	}

	public List<String> getInstructionsDescription() {
		return instructionsDescription == null
				? this.getLanguageSection(getTargetPlayer()).loadStringList("stringListEditor.Instructions",
						Arrays.asList("&7[&fClick&7] &9Left&b/&9Right &7> &9Change line",
								"&7[&fClick&7] &9Shift Left&b/&9Right &7> &9Change action",
								"&7[&fClick&7] &9Middle &7> %action_symbol% &9%action_description%", "%action_line%"))
				: instructionsDescription.get();
	}

	private String actionLine() {
		return new StringBuilder("")
				.append(action == Action.ADD_LINE ? "&b[" + action.symbol + "&b] "
						: "&8[" + Action.ADD_LINE.symbol + "&8] ")
				.append(action == Action.EDIT_LINE ? "&b[" + action.symbol + "&b] "
						: "&8[" + Action.EDIT_LINE.symbol + "&8] ")
				.append(action == Action.DELETE_LINE ? "&b[" + action.symbol + "&b] "
						: "&8[" + Action.DELETE_LINE.symbol + "&8] ")
				.append(action == Action.MOVE_DOWN ? "&b[" + action.symbol + "&b] "
						: "&8[" + Action.MOVE_DOWN.symbol + "&8] ")
				.append(action == Action.MOVE_UP ? "&b[" + action.symbol + "&b] "
						: "&8[" + Action.MOVE_UP.symbol + "&8]")
				.toString();
	}

	private enum Action {
		ADD_LINE("&a&l+", "Add Line"), EDIT_LINE("&9✐", "Edit line"), DELETE_LINE("&c&l✗", "Delete line"),
		MOVE_DOWN("&e▽", "Move line downside"), MOVE_UP("&e△", "Move line upside");

		private final String symbol;
		private final String desc;

		Action(String symbol, String desc) {
			this.symbol = symbol;
			this.desc = desc;
		}

	}

}
