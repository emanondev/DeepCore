package emanondev.core.gui;

import java.util.*;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import emanondev.core.ItemBuilder;

public class StringListEditorButton extends EditorButton<List<String>> {

	private List<String> list = null;
	private int line = -1;

	public StringListEditorButton(Gui gui) {
		super(gui);
		this.setOnClick(new Function<InventoryClickEvent, Boolean>() {

			@Override
			public Boolean apply(InventoryClickEvent t) {
				list = getValue();
				if (list == null) {
					list = new ArrayList<>();
					line = -1;
				} else {
					list = new ArrayList<>(list);
					line = 0;
				}
				////TODO unsupported
				/*
				AnvilInternalGui anvil = new AnvilInternalGui(StringListEditorButton.this.getGui().getPlugin(),
						(Player) t.getWhoClicked(), "§9Editor Gui",
						line == -1 ? "" : UtilsString.revertColors(list.get(line)), craftMainItem(), false,
						new Consumer<InventoryClickEvent>() {

							@Override
							public void accept(InventoryClickEvent t) {
								if (t.getRawSlot() == 0) {
									switch (t.getClick()) {
									case LEFT: {
										if (line == -1)
											return;
										line = (line + 1) % list.size();
										anvil.getInventory().setItem(0, craftMainItem());
										return;
									}
									case RIGHT: {
										if (line == -1)
											return;
										line = (line - 1) % list.size();
										anvil.getInventory().setItem(0, craftMainItem());
										return;
									}
									case SHIFT_RIGHT: {
										ItemMeta meta = t.getView().getTopInventory().getItem(2).getItemMeta();
										// TODO
										// StringListEditorButton.this
										// .changeRequest(meta.hasDisplayName() ? meta.getDisplayName() : "");
										t.getWhoClicked()
												.openInventory(StringListEditorButton.this.getGui().getInventory());
										return;
									}
									case SHIFT_LEFT: {
										t.getWhoClicked()
												.openInventory(StringListEditorButton.this.getGui().getInventory());
										return;
									}
									default:
										break;
									}
									return;
								}

								if (t.getRawSlot() == 2)
									switch (t.getClick()) {
									case LEFT: {
										ItemMeta meta = t.getView().getTopInventory().getItem(2).getItemMeta();
										// TODO
										// StringListEditorButton.this
										// .changeRequest(meta.hasDisplayName() ? meta.getDisplayName() : "");
										t.getWhoClicked()
												.openInventory(StringListEditorButton.this.getGui().getInventory());
										return;
									}
									case SHIFT_LEFT:
										t.getWhoClicked()
												.openInventory(StringListEditorButton.this.getGui().getInventory());
										return;
									default:
										break;
									}
							}

						});
				anvil.getInventory().setItem(1,
						new ItemBuilder(Material.ENCHANTED_BOOK).setGuiProperty()
								.setDescription(Arrays.asList("&6Left Button:", " &3Left Click &7select line &fbelow",
										" &3Right Click &7select line &fupside", " &3Shift Left Click &aadd &fnew line",
										" &3Shift Right Click &cdelete &fcurrent line", "&6Right Button:",
										" &3Left Click &7update line", " &3Right Click &7confirm changes",
										" &3Shift Right Click &7ignore changes"), true)
								.build());
								*/
				return true;
			}
		});
	}

	private ItemStack craftMainItem() {
		ArrayList<String> description = new ArrayList<>();
		description.add("");
		description.add("&6Selected Line: &e" + (line == -1 ? "None" : String.valueOf(line)));
		description.add("&6List:");
		for (int i = 0; i < list.size(); i++)
			description.add((i == line ? "&a⮚ &f" : "&f⮚ ") + list.get(i));
		return new ItemBuilder(Material.TURTLE_HELMET).setGuiProperty().setDescription(description, true)
				.setDisplayName(line == -1 ? "" : list.get(line).replace("§", "&")).build();
	}
}
