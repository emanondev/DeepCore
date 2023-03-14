package emanondev.core;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import emanondev.core.command.CoreCommand;
import emanondev.core.message.DMessage;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TestCommand extends CoreCommand {//TODO

    private final HashMap<String, Integer> counter = new HashMap<>();

    public TestCommand() {
        super("test", CoreMain.get(), null);
        for (String category : List.of("barche", "alberi", "casamedioevale", "castello", "organic", "hub-spawn", "minigamearena", "statua"))
            counter.put(category, 0);
        /*new BukkitRunnable() {
            @Override
            public void run() {
                Player ema = Bukkit.getPlayer("emanon___");
                if (ema == null)
                    return;
                notifyMsg(ema);
            }
        }.runTaskTimer(CoreMain.get(), 20, 60 * 20);*/
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {

            return;
        }
        if (args.length != 1) {
            notifyMsg(player);
            return;
        }

        String category = args[0].toLowerCase(Locale.ENGLISH);
        if (category.equals("copy")) {
            try {
                World world = player.getWorld();
                Region sel = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player))
                        .getSelection(BukkitAdapter.adapt(world));
                Location area = new Location(world, sel.getMinimumPoint().getX(), sel.getMinimumPoint().getY(),
                        sel.getMinimumPoint().getZ());
                player.teleport(area);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.performCommand("/copy ");
                    }
                }.runTaskLater(CoreMain.get(), 5L);
            } catch (Exception e) {
                new DMessage(CoreMain.get(), player).append(DyeColor.RED).append("Area non selezionata").send();
            }
            return;
        }

        try {
            Location original = player.getLocation();
            counter.putIfAbsent(category, 0);
            int id = counter.put(category, counter.get(category) + 1);
            World world = player.getWorld();
            Region sel = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player))
                    .getSelection(BukkitAdapter.adapt(world));
            Location area = new Location(world, sel.getMinimumPoint().getX(), sel.getMinimumPoint().getY(),
                    sel.getMinimumPoint().getZ());
            player.teleport(area);
            runLater(5, () -> player.performCommand("/copy "));
            runLater(20, () -> player.teleport(original));
            runLater(60, () -> player.performCommand(
                    "schematic save " + category + id + "_" + (sel.getMaximumPoint().getX() - sel.getMinimumPoint().getX()) + "x"
                            + (sel.getMaximumPoint().getZ() - sel.getMinimumPoint().getZ()) + "x"
                            + (sel.getMaximumPoint().getY() - sel.getMinimumPoint().getY())));
            runLater(90L, () -> notifyMsg(player));
        } catch (IncompleteRegionException e) {
            new DMessage(CoreMain.get(), player).append(DyeColor.RED).append("Area non selezionata").send();
        }
    }

    private void runLater(long delay, Runnable run) {
        new BukkitRunnable() {
            @Override
            public void run() {
                run.run();
            }
        }.runTaskLater(CoreMain.get(), delay);
    }

    private void notifyMsg(Player player) {
        DMessage msg = new DMessage(CoreMain.get(), player);
        msg.append("\n\n\n");
        for (String cat : counter.keySet())
            msg.append(DyeColor.LIGHT_BLUE).appendRunCommand("/test " + cat, "[" + cat + "]").append(" ");
        msg.send();
    }


    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location loc) {
        return null;
    }

}
