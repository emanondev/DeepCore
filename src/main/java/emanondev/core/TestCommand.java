package emanondev.core;

import emanondev.core.command.CoreCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TestCommand extends CoreCommand {//TODO

    //private final HashMap<String, Integer> counter = new HashMap<>();

    public TestCommand() {
        super("test", CoreMain.get(), null);
        Map< Class<?>,List<Material>> map = new LinkedHashMap<>();
        for (Material mat: Material.values()){
            if (mat.isItem()) {
                ItemMeta item = new ItemStack(mat).getItemMeta();
                if (map.containsKey(item==null?null:item.getClass()))
                    map.get(item==null?null:item.getClass()).add(mat);
                else
                    map.put(item==null?null:item.getClass(),new ArrayList<>(List.of(mat)));
            }
        }
        map.forEach((k,v)-> {
            getPlugin().logInfo((k==null?null:k.getSimpleName())+" "+(k==null?null:Arrays.toString(k.getInterfaces()))+" "+Arrays.toString(v.toArray()));
        });
        Map< Class<? extends BlockState> ,List<Material>> map2 = new LinkedHashMap<>();
        map.forEach((k,v)-> {
            if (k!=null && k.getSimpleName().equals("CraftMetaBlockState")){
                for (Material mat:v) {
                    BlockStateMeta meta = ((BlockStateMeta) new ItemStack(mat).getItemMeta());
                    Class<? extends BlockState> state = meta.getBlockState().getClass();
                    if (map2.containsKey(state))
                        map2.get(state).add(mat);
                    else
                        map2.put(state,new ArrayList<>(List.of(mat)));
                }
            }
        });
        map2.forEach((k,v)-> {
            getPlugin().logInfo((k==null?null:k.getSimpleName())+" "+(k==null?null:Arrays.toString(k.getInterfaces()))+" "+Arrays.toString(v.toArray()));
        });

    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {

    }


    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location loc) {
        return null;
    }

}
