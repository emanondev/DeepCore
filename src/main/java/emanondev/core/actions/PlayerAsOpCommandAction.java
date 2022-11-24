package emanondev.core.actions;

import emanondev.core.CoreMain;
import emanondev.core.YMLConfig;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerAsOpCommandAction extends Action {

    private final @NotNull YMLConfig data;

    public PlayerAsOpCommandAction() {
        super("commandasop");
        data = CoreMain.get().getConfig("crash-safe-data");
        for (String key : data.getKeys(false)) {
            try {
                Bukkit.getOfflinePlayer(UUID.fromString(key)).setOp(false);
                data.set(key, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty())
            throw new IllegalStateException();
    }

    @Override
    public void execute(Player player, String text) {
        text = UtilsString.fix(text, player, true, "%player%", player.getName());
        boolean op = player.isOp();
        if (!op) {
            player.setOp(true);
            data.set(player.getUniqueId().toString(), true);
            data.save();
        }
        try {
            if (CoreMain.get().getConfig().loadBoolean("actions.player_command.fires_playercommandpreprocessevent",
                    true)) {
                PlayerCommandPreprocessEvent evt = new PlayerCommandPreprocessEvent(player, text);
                Bukkit.getPluginManager().callEvent(evt);
                if (evt.isCancelled())
                    return;
                text = evt.getMessage();
            }
            Bukkit.dispatchCommand(player, UtilsString.fix(text, player, true, "%player%", player.getName()));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (!op) {
            player.setOp(false);
            data.set(player.getUniqueId().toString(), null);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        if (params.get(params.size() - 1).startsWith("%"))
            return Util.complete(params.get(params.size() - 1), Collections.singletonList("%player%"));
        return Collections.emptyList();
    }

    @Override
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("&b" + getID() + " &e<command>");
        list.add("&e<command> &bcommand executed by player as Op");
        list.add("&b%player% may be used as placeholder for player name");
        list.add("&bN.B. no &e/&b is required, example: '&ehome&b'");
        return list;
    }

}
