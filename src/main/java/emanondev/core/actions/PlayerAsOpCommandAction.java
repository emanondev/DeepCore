package emanondev.core.actions;

import emanondev.core.CoreMain;
import emanondev.core.YMLConfig;
import emanondev.itemedit.UtilsString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

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
        } finally {
            if (!op) {
                player.setOp(false);
                data.set(player.getUniqueId().toString(), null);
            }
        }
    }

    @Override
    public String getInfo() {
        return "commandasop <command>";
    }

}
