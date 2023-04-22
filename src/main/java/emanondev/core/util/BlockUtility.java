package emanondev.core.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockUtility {

    private BlockUtility(){
        throw new AssertionError();
    }

    public static boolean setColor(Block block, DyeColor color){
        int index = block.getType().name().indexOf("_",                 block.getType().name().startsWith("LIGHT_") ? 7 : 1);
        if (index > 0)
            try {
                switch (block.getType().name().substring(index)) {
                    case "_BANNER", "_BED", "_CANDLE", "_CARPET", "_CONCRETE", "_CONCRETE_POWDER", "_GLAZED_TERRACOTTA", "_SHULKER_BOX", "_STAINED_GLASS",
                            "_STAINED_GLASS_PANE", "_TERRACOTTA", "_WOOL" -> {
                        block.setType(Material.getMaterial(color.name() + block.getType().name().substring(index)));
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return false;
    }
}
