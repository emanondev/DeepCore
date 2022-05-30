package emanondev.core.util.items;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pro.dracarys.DracarysGUI.api.DracarysGUIAPI;

public class DrcGuiItemConverter implements ItemConverter {

    @Override
    public ItemBuilder convert(@NotNull YMLSection section) {
        ItemStack item = DracarysGUIAPI.parseItem("dracarysgui");
        if (item==null || item.getType().isAir())
            return null;
        return new ItemBuilder(item);
    }
}
