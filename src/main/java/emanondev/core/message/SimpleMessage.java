package emanondev.core.message;

import emanondev.core.CorePlugin;
import emanondev.core.UtilsMessages;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SimpleMessage {

    private final String pathMessage;
    private final CorePlugin plugin;

    public SimpleMessage(@NotNull CorePlugin plugin, @NotNull String path) {
        this.pathMessage = path;
        this.plugin = plugin;
    }

    public void send(CommandSender target, String... placeholders) {
        new DMessage(plugin, target).appendLang(pathMessage, placeholders).send();
    }

    public void send(Collection<? extends CommandSender> targets, String... placeholders) {
        targets.forEach((target) -> send(target, placeholders));
    }

    public void sendActionBar(CommandSender target, String... placeholders) {
        new DMessage(plugin, target).appendLang(pathMessage, placeholders).sendActionBar(target);
    }

    public void sendActionBar(Collection<? extends CommandSender> targets, String... placeholders) {
        targets.forEach((target) -> sendActionBar(target, placeholders));
    }

    public void sendAsSubTitle(Player target, int fadeIn, int stayTime, int fadeOut, String... placeholders) {
        target.sendTitle(" ", new DMessage(plugin, target).appendLang(pathMessage, placeholders).toLegacy(), fadeIn, stayTime, fadeOut);
    }


    public void sendAsSubTitle(Collection<? extends Player> targets, int fadeIn, int stayTime, int fadeOut, String... placeholders) {
        targets.forEach((target) -> sendAsSubTitle(target, fadeIn, stayTime, fadeOut, placeholders));
    }

    public static void clearTitle(@NotNull Player player) {
        player.sendTitle(" ", "", 0, 1, 0);
    }

    public static void sendEmptyActionBarMessage(@NotNull Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
    }

}
