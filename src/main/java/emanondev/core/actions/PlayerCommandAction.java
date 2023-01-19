package emanondev.core.actions;

import emanondev.core.CoreMain;
import emanondev.itemedit.UtilsString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

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
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("&b" + getID() + " &e<command>");
        list.add("&e<command> &bcommand executed by player");
        list.add("&b%player% may be used as placeholder for player name");
        list.add("&bN.B. no &e/&b is required, example: '&ehome&b'");
        return list;
    }


}
