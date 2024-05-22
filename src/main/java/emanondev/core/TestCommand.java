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

    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {

    }


    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location loc) {
        return null;
    }

}
