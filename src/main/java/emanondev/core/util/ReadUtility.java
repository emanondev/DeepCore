package emanondev.core.util;

import emanondev.core.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReadUtility {

    static @Nullable Player readPlayerValue(CommandSender sender, @NotNull String arg) {
        Player p = Bukkit.getPlayer(arg);
        if (p == null)
            return null;
        if (Hooks.isVanishEnabled()) {
            try {
                if (sender instanceof Player)
                    return VanishApi.canSee(sender, p) ? p : null;
                return VanishApi.isInvisible(p) ? null : p;
            } catch (Exception e) {
                e.printStackTrace();
                boolean vanished = false;
                for (MetadataValue meta : p.getMetadata("vanished")) {
                    if (meta.asBoolean())
                        vanished = true;
                }
                return vanished ? null : p;
            }
        }
        return p;
    }

    static @Nullable OfflinePlayer readOfflinePlayerValue(@NotNull String arg) {
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
        if (player.getLastPlayed() == 0)
            return null;
        return player;
    }

    static @Nullable Integer readIntValue(@NotNull String arg) {
        try {
            return Integer.valueOf(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static @Nullable Double readDoubleValue(@NotNull String arg) {
        try {
            return Double.valueOf(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }

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
        if (Hooks.isVanishEnabled()) {
            try {
                return VanishApi.isInvisible(p) ? null : p;
            } catch (Exception e) {//HOTFIX
                e.printStackTrace();
                boolean vanished = false;
                for (MetadataValue meta : p.getMetadata("vanished")) {
                    if (meta.asBoolean())
                        vanished = true;
                }
                if (!vanished)
                    return null;
            }
            return null;
        }
        return p;
    }

    /**
     * @param sender who sended the command
     * @param arg    argument to read
     * @return target player or null if player is offline or vanished
     */
    default @Nullable Player readPlayer(CommandSender sender, @NotNull String arg) {
        return readPlayerValue(sender, arg);
    }

    /**
     * @param arg argument to read
     * @return target player or null if player never joined the server
     */
    default @Nullable OfflinePlayer readOfflinePlayer(@NotNull String arg) {
        return readOfflinePlayerValue(arg);
    }

    /**
     * @param arg argument to read
     * @return int value of the string or null if parsing failed
     */
    default @Nullable Integer readInt(@NotNull String arg) {
        return readIntValue(arg);
    }

    /**
     * @param arg argument to read
     * @return double value of the string or null if parsing failed
     */
    default @Nullable Double readDouble(@NotNull String arg) {
        return readDoubleValue(arg);
    }


}
