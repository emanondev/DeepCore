package emanondev.core.actions;

import emanondev.itemedit.UtilsString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ServerCommandAction extends Action {

    public ServerCommandAction() {
        super("servercommand");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty())
            throw new IllegalStateException();
    }

    @Override
    public void execute(Player player, String text) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UtilsString.fix(text, player, true, "%player%", player.getName()));
    }


    @Override
    public String getInfo() {
        return "command <command>";
    }
}