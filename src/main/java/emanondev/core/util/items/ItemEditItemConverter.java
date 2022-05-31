package emanondev.core.util.items;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.itemedit.ItemEdit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemEditItemConverter implements ItemConverter {

    @Override
    public ItemBuilder convert(@NotNull YMLSection section) {
        String id = section.getString("serveritem", null);
        if (id == null)
            return null;
        ItemStack item = ItemEdit.get().getServerStorage().getItem(id);
        return item == null ? null : new ItemBuilder(item);
    }

}
