package emanondev.core.actions;

import org.bukkit.entity.Player;

public class PermissionAction extends Action {

    public PermissionAction() {
        super("permission");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty())
            throw new IllegalStateException();
        String[] args = text.split(" ");
        if (args.length < 3)
            throw new IllegalStateException();

        ActionHandler.validateActionType(args[1]);
        ActionHandler.validateActionInfo(args[1], text.substring(args[0].length() + args[1].length() + 2));
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        if (args[0].startsWith("-")) {
            if (player.hasPermission(args[0].substring(1)))
                return;
        } else if (!player.hasPermission(args[0]))
            return;
        ActionHandler.handleAction(player, args[1], text.substring(args[0].length() + args[1].length() + 2));

    }

    @Override
    public String getInfo() {
        return "permission <permission> <action>";
    }
}