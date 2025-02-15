package emanondev.core.util;

import emanondev.core.Hooks;
import emanondev.core.utility.ReadHelper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface ReadUtility extends ReadHelper {

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
        if (player.getLastPlayed() == 0 && !player.isOnline())
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

    static @Nullable Boolean readBooleanValue(@NotNull String arg) {
        if (arg.equalsIgnoreCase("true"))
            return Boolean.TRUE;
        if (arg.equalsIgnoreCase("false"))
            return Boolean.FALSE;
        return null;
    }

    static @Nullable Double readDoubleValue(@NotNull String arg) {
        try {
            return Double.valueOf(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
