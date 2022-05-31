package emanondev.core;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UtilsMessages {

    private UtilsMessages() {
        throw new AssertionError();
    }

    /**
     * @param target  Who will receive the message
     * @param message Message to send. If it is empty ("") the actionbar is
     *                cleared.
     */
    public static void sendActionbar(@NotNull Player target, @Nullable String message) {
        if (message == null)
            return;
        //1.10+
        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));

    }

    /**
     * Message is not send if message is null or empty
     *
     * @param target  Who will receive the message
     * @param message Message to send
     */
    public static void sendMessage(@NotNull CommandSender target, @Nullable String message) {
        if (message == null || message.isEmpty())
            return;
        target.sendMessage(message);
    }

    /**
     * Message is not send if message is null or empty
     *
     * @param target  Who will receive the message
     * @param message Message to send
     */
    public static void sendMessage(@NotNull CommandSender target, @Nullable BaseComponent[] message) {
        if (message == null || message.length == 0)
            return;
        target.spigot().sendMessage(message);
    }

}
