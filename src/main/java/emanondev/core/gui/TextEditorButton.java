package emanondev.core.gui;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class TextEditorButton extends AGuiButton {

    private ItemStack baseItem = new ItemBuilder(Material.PAPER).setGuiProperty().build();

    public TextEditorButton(Gui parent) {
        super(parent);
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        if (event.getClick() == ClickType.LEFT)
            new TextEditorGui(getTitle(), getValue(), (Player) event.getWhoClicked(), getGui(), getPlugin()) {

                @Override
                public void onTextConfirmed(String line) {
                    TextEditorButton.this.onTextChange(line);
                    TextEditorButton.this.getGui().open(event.getWhoClicked());
                }

            }.open(event.getWhoClicked());
        return false;
    }

    @Override
    public ItemStack getItem() {
        ItemStack baseItem = getBaseItem();
        if (baseItem == null)
            return null;
        return new ItemBuilder(baseItem).setDescription(getDescription(), true).build();
    }

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        desc.addAll(getBaseDescription());
        desc.addAll(getValueDescription());
        desc.addAll(getInstructionsDescription());
        return UtilsString.fix(desc, null, true);
    }

    public List<String> getBaseDescription() {
        return List.of("&6&lText:");
    }

    public abstract String getValue();

    public abstract void onTextChange(String text);

    public List<String> getValueDescription() {
        String text = getValue();
        List<String> val = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            val.add("");
            return val;
        }
        return UtilsString.textLineSplitter(text);
    }

    public List<String> getInstructionsDescription() {
        return List.of("&7[&fClick&7] &9Left &7> &9Change Text");
    }

    public void setBaseItem(ItemStack item) {
        baseItem = item;
    }

    public ItemStack getBaseItem() {
        return baseItem;
    }

    public void setTitle(String value) {
        title = value;
    }

    public String getTitle() {
        return title;
    }

    private String title = "&9Text editor";
}
