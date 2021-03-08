package emanondev.core.gui;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import emanondev.core.gui.anvil.AnvilInternalGui;

public class StringEditorButton extends EditorButton<String> {

	public StringEditorButton(Gui gui) {
		super(gui);
		this.setOnClick(new Function<InventoryClickEvent, Boolean>(){

			@Override
			public Boolean apply(InventoryClickEvent t) {
				new AnvilInternalGui(StringEditorButton.this.getGui().getPlugin(), (Player) t.getWhoClicked(), "§9Rename Gui",
						UtilsString.revertColors(StringEditorButton.this.getValue()),
						new ItemBuilder(Material.NAME_TAG).setGuiProperty()
								.setDescription(Arrays.asList("", "&6Original Name:", StringEditorButton.this.getValue(), "",
										"&3Left Click &7to &fupdate value", "&3Shift Right Click &7to &fundo"), true)
								.build(),
						false, new Consumer<InventoryClickEvent>() {

							@Override
							public void accept(InventoryClickEvent t) {
								if (t.getRawSlot() == 0 || t.getRawSlot() == 2)
									switch (t.getClick()) {
									case LEFT: {
										ItemMeta meta = t.getView().getTopInventory().getItem(2).getItemMeta();
										StringEditorButton.this
												.changeRequest(meta.hasDisplayName() ? meta.getDisplayName() : "");
										t.getWhoClicked().openInventory(StringEditorButton.this.getGui().getInventory());
										return;
									}
									case SHIFT_LEFT:
										t.getWhoClicked().openInventory(StringEditorButton.this.getGui().getInventory());
										return;
									default:
										break;
									}
							}

						});
				return true;
			}
		});
	}
}
