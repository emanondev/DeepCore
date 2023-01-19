package emanondev.core.actions;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("&b" + getID() + " &e<permission> <action>");
        list.add("&e<permission> &bpermission required or &e-permission &bfor reversed check");
        list.add("&e<action> &baction to execute, example: '&esound entity_bat_hurt 1 1&b'");
        return list;
    }
}