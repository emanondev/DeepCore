package emanondev.core.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;

public class TextEditorFButton extends AGuiButton {

    private final Supplier<String> getValue;
    private final Consumer<String> setValue;
    private final Supplier<ItemStack> baseItem;
    private final Supplier<List<String>> baseDescription;
    private final Supplier<List<String>> valueDescription;
    private final Supplier<List<String>> instructionsDescription;

    /**
     * @param parent
     * @param getValue
     * @param setValue
     * @param baseItem
     * @param baseDescription
     * @param valueDescription
     * @param instructionsDescription
     */
    public TextEditorFButton(Gui parent, @NotNull Supplier<String> getValue, @NotNull Consumer<String> setValue,
                             @Nullable Supplier<ItemStack> baseItem, @Nullable Supplier<List<String>> baseDescription,
                             @Nullable Supplier<List<String>> valueDescription,
                             @Nullable Supplier<List<String>> instructionsDescription) {
        super(parent);
        this.getValue = getValue;
        this.setValue = setValue;
        this.baseItem = baseItem;
        this.baseDescription = baseDescription;
        this.valueDescription = valueDescription;
        this.instructionsDescription = instructionsDescription;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        new TextEditorGui(getTitle(), getValue(), (Player) event.getWhoClicked(), getGui(), getPlugin()) {

            @Override
            public void onTextConfirmed(String line) {
                TextEditorFButton.this.setValue(line);
                TextEditorFButton.this.getGui().open(event.getWhoClicked());
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
        List<String> tmp = getBaseDescription();
        if (tmp != null)
            desc.addAll(getBaseDescription());
        tmp = getValueDescription();
        if (tmp != null)
            desc.addAll(tmp);
        tmp = getInstructionsDescription();
        if (tmp != null)
            desc.addAll(tmp);
        return UtilsString.fix(desc, null, true);
    }

    public List<String> getBaseDescription() {
        return baseDescription == null ? List.of("&6&lText:") : baseDescription.get();
    }

    public String getValue() {
        return getValue.get();
    }

    public void setValue(String value) {
        setValue.accept(value);
    }

    public ItemStack getBaseItem() {
        return baseItem == null ? new ItemBuilder(Material.PAPER).setGuiProperty().build() : baseItem.get();
    }

    public List<String> getValueDescription() {
        return valueDescription == null ? List.of(getValue() == null ? "" : getValue()) : valueDescription.get();
    }

    public List<String> getInstructionsDescription() {
        return instructionsDescription == null ? this.getLanguageSection(getTargetPlayer())
                .loadStringList("textEditor.Instructions", List.of("&7[&fClick&7] &9Any &7> &9Change Text"))
                : instructionsDescription.get();
    }

    public void setTitle(String value) {
        title = value;
    }

    public String getTitle() {
        return title;
    }

    private String title = "&9Text editor";
}
