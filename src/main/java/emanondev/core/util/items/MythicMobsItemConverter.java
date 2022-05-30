package emanondev.core.util.items;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MythicMobsItemConverter implements ItemConverter {
    @Override
    public ItemBuilder convert(@NotNull YMLSection section) {
        String item = section.getString("mythicmobs");
        int amount = 1;
        String itemName = item;
        if (item.contains(";")) {
            String[] parts = item.split(";");
            itemName = parts[0];
            amount = Integer.parseInt(parts[1]);
        }

        ItemStack finalItem = MythicMobs.inst().getItemManager().getItemStack(itemName);


        return finalItem == null?null: new ItemBuilder(finalItem).setAmount(amount);
    }
}
