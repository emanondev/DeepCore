package emanondev.core.gui;

import emanondev.core.CoreMain;
import emanondev.core.ItemBuilder;
import emanondev.core.message.DMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StringEditorFButton extends AGuiButton {

    private final Supplier<String> getValue;
    private final Consumer<String> setValue;
    private final Supplier<ItemStack> baseItem;
    private final boolean addInstructions;

    /**
     * @param parent
     * @param getValue
     * @param setValue
     * @param baseItem
     * @param addInstructions
     */
    public StringEditorFButton(@NotNull Gui parent, @NotNull Supplier<String> getValue, @NotNull Consumer<String> setValue,
                               @Nullable Supplier<ItemStack> baseItem,
                               boolean addInstructions) {
        super(parent);
        this.getValue = getValue;
        this.setValue = setValue;
        this.baseItem = baseItem;
        this.addInstructions = addInstructions;
    }

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        new TextEditorGui(getTitle(), getValue(), (Player) event.getWhoClicked(), getGui(), getPlugin()) {

            @Override
            public void onTextConfirmed(String line) {
                StringEditorFButton.this.setValue(line);
                StringEditorFButton.this.getGui().open(event.getWhoClicked());
            }

        }.open(event.getWhoClicked());
        return false;
    }

    @Override
    public ItemStack getItem() {
        ItemStack base = getBaseItem();
        if (base == null)
            return null;
        ItemBuilder builder = new ItemBuilder(base);
        if (this.addInstructions)
            return builder.addDescription(getInstructionsDescription()).build();
        return builder.build();
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

    public DMessage getInstructionsDescription() {
        return new DMessage(CoreMain.get(), getTargetPlayer()).appendLang("textEditor.Instructions");
        //, List.of("&7[&fClick&7] &9Any &7> &9Change Text"))
    }

    public void setTitle(String value) {
        title = value;
    }

    public String getTitle() {
        return title;
    }

    private String title = "&9Text editor";
}
