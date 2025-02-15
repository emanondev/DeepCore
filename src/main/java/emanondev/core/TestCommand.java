package emanondev.core;

import emanondev.core.command.CoreCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
