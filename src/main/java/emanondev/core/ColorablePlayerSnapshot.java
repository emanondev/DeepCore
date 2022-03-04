package emanondev.core;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

public class ColorablePlayerSnapshot extends PlayerSnapshot {

    public ColorablePlayerSnapshot(@NotNull PlayerSnapshot parent, @NotNull DyeColor color, boolean blocks, boolean leather) {
        for (FieldType type : FieldType.values())
            this.set(type, parent.get(type));
        List<ItemStack> items;
        ItemFactory factory = Bukkit.getServer().getItemFactory();
        LeatherArmorMeta colorMeta = (LeatherArmorMeta) factory.getItemMeta(Material.LEATHER_BOOTS);
        colorMeta.setColor(color.getColor());
        if (this.getInventory() != null) {
            items = new ArrayList<>(this.getInventory());
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) == null)
                    continue;
                if (blocks) {
                    String matName = items.get(i).getType().name();
                    for (DyeColor c : DyeColor.values())
                        try {
                            if (c != color && matName.contains(c.name() + "_")) {
                                ItemStack item = new ItemStack(items.get(i));
                                item.setType(Material.valueOf(matName.replace(c.name() + "_", color.name() + "_")));
                                items.set(i, item);
                                break;
                            }
                        } catch (Exception e) {

                        }
                }
                if (leather && factory.isApplicable(colorMeta, items.get(i).getType()))
                    items.set(i, new ItemBuilder(items.get(i)).setColor(colorMeta.getColor()).build());

            }
            this.setInventory(items);
        }
        if (this.getArmor() != null) {
            items = new ArrayList<>(this.getArmor());
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) == null)
                    continue;
                if (blocks) {
                    String matName = items.get(i).getType().name();
                    for (DyeColor c : DyeColor.values())
                        try {
                            if (matName.contains(c.name() + "_")) {
                                ItemStack item = new ItemStack(items.get(i));
                                item.setType(Material.valueOf(matName.replace(c.name() + "_", color.name() + "_")));
                                items.set(i, item);
                                break;
                            }
                        } catch (Exception e) {

                        }
                }
                if (leather && factory.isApplicable(colorMeta, items.get(i).getType()))
                    items.set(i, new ItemBuilder(items.get(i)).setColor(colorMeta.getColor()).build());

            }
            this.setArmor(items);
        }
        if (this.getEnderChest() != null) {
            items = new ArrayList<>(this.getEnderChest());
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) == null)
                    continue;
                if (blocks) {
                    String matName = items.get(i).getType().name();
                    for (DyeColor c : DyeColor.values())
                        try {
                            if (matName.contains(c.name() + "_")) {
                                ItemStack item = new ItemStack(items.get(i));
                                item.setType(Material.valueOf(matName.replace(c.name() + "_", color.name() + "_")));
                                items.set(i, item);
                                break;
                            }
                        } catch (Exception e) {

                        }
                }
                if (leather && factory.isApplicable(colorMeta, items.get(i).getType()))
                    items.set(i, new ItemBuilder(items.get(i)).setColor(colorMeta.getColor()).build());

            }
            this.setEnderChest(items);
        }

    }

}
