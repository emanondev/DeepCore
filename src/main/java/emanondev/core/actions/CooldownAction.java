package emanondev.core.actions;

import emanondev.core.CoreMain;
import emanondev.core.UtilsString;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class CooldownAction extends Action {
    public CooldownAction() {
        super("cooldown");
    }

    @Override
    public void validateInfo(String text) {
        String[] args = text.split(" ");
        if (!UtilsString.isValidID(args[0]) || Integer.parseInt(args[1]) < 0)
            throw new IllegalStateException();
        if (args.length != 3 && args.length != 2)
            throw new IllegalStateException();
        if (args.length == 3 && !args[2].equals("true") && !args[2].equals("false"))
            throw new IllegalStateException();
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        CoreMain.get().getCooldownAPI(args.length < 3 || Boolean.parseBoolean(args[2]))
                .setCooldown(player, args[0], Integer.parseInt(args[1]), TimeUnit.MILLISECONDS);
    }

    @Override
    public String getInfo() {
        return "cooldown <cooldown id> <ccoldown ms> [persistent=true]";
    }
}
