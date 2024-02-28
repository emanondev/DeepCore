package emanondev.core.gui;

import emanondev.core.CorePlugin;
import emanondev.core.message.DMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AdvancedResearchFGui<T> extends AdvancedResearchGui<T> {


    private final BiFunction<String, T, Boolean> mather;
    private final BiFunction<InventoryClickEvent, T, Boolean> onClick;
    private final Function<T, ItemStack> getItem;

    public AdvancedResearchFGui(@Nullable DMessage title, Player player, @Nullable Gui previousHolder, @NotNull CorePlugin plugin, ItemStack base,
                                @NotNull BiFunction<String, T, Boolean> matcher, @NotNull BiFunction<InventoryClickEvent, T, Boolean> onClick, @NotNull Function<T, ItemStack> getItem,
                                @Nullable Collection<T> elements) {
        super(title, player, previousHolder, plugin, base);
        this.mather = matcher;
        this.onClick = onClick;
        this.getItem = getItem;
        if (elements != null)
            this.addElements(elements);
    }

    @Override
    public boolean match(String text, T value) {
        return mather.apply(text, value);
    }

    @Override
    public boolean onElementClick(InventoryClickEvent event, T value) {
        return onClick.apply(event, value);
    }

    @Override
    public ItemStack getElementItem(T value) {
        return getItem.apply(value);
    }
}