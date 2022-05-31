package emanondev.core.util;

import de.myzelyam.api.vanish.VanishAPI;
import emanondev.core.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReadUtility {

    /**
     * @param arg argument to read
     * @return target player or null if player is offline or vanished
     * @see #readPlayer(CommandSender, String)
     */
    @Deprecated
    default @Nullable Player readPlayer(@NotNull String arg) {
        Player p = Bukkit.getPlayer(arg);
        if (p == null)
            return null;
        if (Hooks.isVanishEnabled())
            return VanishAPI.isInvisible(p) ? null : p;
        return p;
    }

    /**
     * @param sender who sended the command
     * @param arg    argument to read
     * @return target player or null if player is offline or vanished
     */
    default @Nullable Player readPlayer(CommandSender sender, @NotNull String arg) {
        Player p = Bukkit.getPlayer(arg);
        if (p == null)
            return null;
        if (Hooks.isVanishEnabled()) {
            if (sender instanceof Player)
                return VanishAPI.canSee((Player) sender, p) ? p : null;
            return VanishAPI.isInvisible(p) ? null : p;
        }
        return p;
    }

    /**
     * @param arg argument to read
     * @return target player or null if player never joined the server
     */
    default @Nullable OfflinePlayer readOfflinePlayer(@NotNull String arg) {
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
        if (player.getLastPlayed() == 0)
            return null;
        return player;
    }

    /**
     * @param arg argument to read
     * @return int value of the string or null if parsing failed
     */
    default @Nullable Integer readInt(@NotNull String arg) {
        try {
            return Integer.valueOf(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * @param arg argument to read
     * @return double value of the string or null if parsing failed
     */
    default @Nullable Double readDouble(@NotNull String arg) {
        try {
            return Double.valueOf(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }


}
