package emanondev.core.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import emanondev.core.CorePlugin;
import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;

public abstract class TextEditorGui extends AnvilGui {

	private String original;
	private String storedText = "";
	private String lastText;

	public TextEditorGui(@Nullable String title, String baseLine, @Nullable Player p, @Nullable Gui previusHolder,
			@NotNull CorePlugin plugin) {
		super(title, p, previusHolder, plugin);
		if (baseLine == null)
			baseLine = "";
		setOriginal(baseLine);
		setLine(baseLine);
	}

	public TextEditorGui(@Nullable String title, String baseLine, @Nullable Player p, @Nullable Gui previusHolder,
			@NotNull CorePlugin plugin, boolean isTimerUpdated) {
		super(title, p, previusHolder, plugin, isTimerUpdated);
		original = baseLine;

	}

	@Override
	public void onTextChange(String newText) {
		if (newText == null)
			newText = "";
		if (storedText.isEmpty() && newText.startsWith(">"))
			newText = newText.substring(1);

		if (newText.equals(lastText))
			return;
		if (storedText.isEmpty())
			lastText = ">" + newText;
		else
			lastText = newText;

		if (lastText.length() < 8) {
			if (!storedText.isEmpty()) {
				while (lastText.length() < 16 && !storedText.isEmpty()) { // lazy slow way, can be better
					lastText = storedText.substring(storedText.length() - 1) + lastText;
					storedText = storedText.substring(0, storedText.length() - 1);
				}
				updateInventory();
			}
			return;
		}

		if (lastText.length() > 30) {
			storedText = storedText + lastText.substring(0, 10);
			lastText = lastText.substring(10);
			updateInventory();
		}

	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if (!event.getClickedInventory().equals(getInventory()))
			return;
		switch (event.getSlot()) {
		case 0:
			switch (event.getClick()) {
			case LEFT:
				updateInventory();
				return;
			case RIGHT:
				this.setLine("");
				return;
			case MIDDLE:
				this.onTextConfirmed(getLine());
				return;
			case SHIFT_LEFT:
			case SHIFT_RIGHT:
				this.setLine(getOriginal());
				return;
			default:
				break;
			}
			return;
		case 1:
			back.onClick(event);
			return;
		case 2:
		}
	}

	public abstract void onTextConfirmed(String line);

	private BackButton back = new BackButton(this);

	@Override
	public @Nullable GuiButton getButton(int slot) {
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
		this.getInventory().setItem(0, getItemZero());
		this.getInventory().setItem(1, back.getItem());
	}

	public String getLine() {
		String line = storedText + lastText;
		if (line.startsWith(">"))
			line = line.substring(1);
		return line;
	}

	public void setLine(String line) {
		if (line.length() > 29) {
			storedText = ">" + line.substring(0, line.length() - 29);
			lastText = line.substring(line.length() - 29);
		} else {
			lastText = ">" + line;
		}
		updateInventory();
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String value) {
		if (value == null)
			original = "";
		else
			original = value.replace("§", "&");
	}

	private List<String> format(String info, String text) {
		List<String> list = new ArrayList<>();
		list.add(UtilsString.fix("&b[&9" + info + "&b]", null, true));
		if (text.isEmpty()) {
			list.add("");
			return list;
		}
		list.addAll(UtilsString.textLineSplitter(text));
		String line = text;
		
		text = UtilsString.fix(line, null, true);
		if (line.equals(text))
			return list;

		list.add(UtilsString.fix("&b[&9" + info + " with Color&b]", null, true));
		list.addAll(UtilsString.textLineSplitter(text));
		return list;
	}

	public ItemStack getItemZero() {
		String line = getLine();
		List<String> desc = new ArrayList<>();
		desc.add(lastText);
		desc.addAll(format("Original", original));
		desc.addAll(format("Changes", line));

		desc.addAll(UtilsString.fix(Arrays.asList("", "&7[&fClick&7] &9Left &7> &eUpdate",
				"&7[&fClick&7] &9Middle &7> &aUpdate and Confirm changes", "&7[&fClick&7] &9Right &7> &cEmpty line",
				"&7[&fClick&7] &9Shift Left/Right &7> &cRevert to original"), null, true));
		return new ItemBuilder(Material.PAPER).setDescription(desc, false).build();
	}

}
