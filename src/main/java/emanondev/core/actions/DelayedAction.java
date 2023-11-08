package emanondev.core.actions;

import emanondev.core.CoreMain;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedAction extends Action {

    public DelayedAction() {
        super("delay");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty())
            throw new IllegalStateException();
        String[] args = text.split(" ");
        if (args.length < 3)
            throw new IllegalStateException();

        long ticks = Long.parseLong(args[0]);
        if (ticks <= 0)
            throw new IllegalStateException();

        ActionHandler.validateActionType(args[1]);
        ActionHandler.validateActionInfo(args[1], text.substring(args[0].length() + args[1].length() + 2));
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        new BukkitRunnable() {
            public void run() {
                ActionHandler.handleAction(player, args[1], text.substring(args[0].length() + args[1].length() + 2));
            }
        }.runTaskLater(CoreMain.get(), Long.parseLong(args[0]));
    }

    @Override
    public String getInfo() {
        return "delay <delay ticks> <action>";
    }
}
