package emanondev.core.actions;

import emanondev.core.CoreMain;
import emanondev.itemedit.UtilsString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;

public class PlayerCommandAction extends Action {

    public PlayerCommandAction() {
        super("command");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty())
            throw new IllegalStateException();
    }

    @Override
    public void execute(Player player, String text) {
        text = UtilsString.fix(text, player, true, "%player%", player.getName());
        if (CoreMain.get().getConfig().loadBoolean("actions.player_command.fires_playercommandpreprocessevent", true)) {
            PlayerCommandPreprocessEvent evt = new PlayerCommandPreprocessEvent(player, text);
            Bukkit.getPluginManager().callEvent(evt);
            if (evt.isCancelled())
                return;
            text = evt.getMessage();
        }
        Bukkit.dispatchCommand(player, UtilsString.fix(text, player, true, "%player%", player.getName()));
    }


    @Override
    public String getInfo() {
        return "command <command>"; //no /
    }


}
