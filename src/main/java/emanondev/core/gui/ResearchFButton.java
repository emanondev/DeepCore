package emanondev.core.gui;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import emanondev.core.ItemBuilder;

public class ResearchFButton<T> extends AGuiButton {

    private final Supplier<ItemStack> getItem;
    private final Function<InventoryClickEvent, Boolean> onClick;
    private final BiFunction<String, T, Boolean> match;
    private final BiFunction<InventoryClickEvent, T, Boolean> elementClick;
    private final Function<T, ItemStack> elementItem;
    private final Supplier<Collection<T>> getElements;
    private final Comparator<T> sort;

    /**
     * @param parent
     * @param getItem      create the item for this button
     * @param match        select which string match which elements
     * @param elementClick what happens when clicked
     * @param elementItem  create the item for element
     * @param getElements  get allowed values
     */
    public ResearchFButton(Gui parent, @NotNull Supplier<ItemStack> getItem,
                           @NotNull BiFunction<String, T, Boolean> match,
                           @NotNull BiFunction<InventoryClickEvent, T, Boolean> elementClick,
                           @NotNull Function<T, ItemStack> elementItem, Supplier<Collection<T>> getElements) {
        this(parent, getItem, null, match, elementClick, elementItem, getElements, null);
    }

    /**
     * @param parent
     * @param getItem      create the item for this button
     * @param onClick      override click
     * @param match        select which string match which elements
     * @param elementClick what happens when clicked
     * @param elementItem  create the item for element
     * @param getElements  get allowed values
     * @param sort         how to sort entities
     */
    public ResearchFButton(Gui parent, @NotNull Supplier<ItemStack> getItem,
                           Function<InventoryClickEvent, Boolean> onClick, @NotNull BiFunction<String, T, Boolean> match,
                           @NotNull BiFunction<InventoryClickEvent, T, Boolean> elementClick,
                           @NotNull Function<T, ItemStack> elementItem, Supplier<Collection<T>> getElements, Comparator<T> sort) {
        super(parent);
        if (getItem == null || match == null || elementClick == null || elementItem == null || getElements == null)
            throw new NullPointerException();
        this.getItem = getItem;
        this.onClick = onClick;
        this.match = match;
        this.elementClick = elementClick;
        this.elementItem = elementItem;
        this.getElements = getElements;
        this.sort = sort;
    }

    public ItemStack getItem() {
        return getItem.get();
    }

    public boolean onClick(@NotNull InventoryClickEvent event) {
        return onClick == null ? defaultOnClick(event) : onClick.apply(event);
    }

    public boolean defaultOnClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT)
            return false;
        AdvancedResearchGui<T> gui = new AdvancedResearchGui<>("Research",
                new ItemBuilder(Material.PAPER).setGuiProperty().build(), (Player) event.getWhoClicked(), getGui(),
                getGui().getPlugin()) {

            @Override
            public boolean match(String text, T value) {
                return match.apply(text, value);
            }

            @Override
            public boolean onElementClick(InventoryClickEvent event, T value) {
                return elementClick.apply(event, value);
            }

            @Override
            public ItemStack getElementItem(T value) {
                return elementItem.apply(value);
            }

        };
        gui.addElements(getElements.get());
        cButtons.forEach((s, f) -> gui.setControlGuiButton(s, f.apply(gui)));
        if (sort != null)
            gui.sort(sort);
        gui.open(event.getWhoClicked());
        return false;
    }

    private final HashMap<Integer, Function<Gui, GuiButton>> cButtons = new HashMap<>();

    public void setControlButton(int slot, Function<Gui, GuiButton> controlButton) {
        if (slot < 0 || slot > 8)
            throw new IllegalArgumentException();
        if (controlButton == null)
            cButtons.remove(slot);
        else
            cButtons.put(slot, controlButton);
    }

}
