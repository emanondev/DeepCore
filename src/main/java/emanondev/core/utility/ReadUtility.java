package emanondev.core.utility;

import emanondev.core.Hooks;
import emanondev.core.util.VanishApi;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ReadUtility {

    private ReadUtility() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    static Player readPlayer(final CommandSender sender, final @NotNull String arg) {
        Player target = Bukkit.getPlayer(arg);
        if (target == null) {
            return null;
        }
        if (Hooks.isVanishEnabled()) {
            return VanishApi.canSee(sender, target) ? target : null;
        }
        return target;
    }

    @Nullable
    static OfflinePlayer readOfflinePlayer(final @NotNull String arg) {
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
        if (player.getLastPlayed() == 0 && !player.isOnline())
            return null;
        return player;
    }

    @Nullable
    static Integer readInt(final @NotNull String arg) {
        try {
            return Integer.valueOf(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    static Boolean readBoolean(final @NotNull String arg) {
        if (arg.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (arg.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Nullable
    static Double readDouble(final @NotNull String arg) {
        try {
            return Double.valueOf(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    static <T extends Enum<T>> T readEnum(final @NotNull String arg,
                                          final @NotNull Class<T> clazz) {
        try {
            return Enum.valueOf(clazz, arg.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            try {
                return Enum.valueOf(clazz, arg);
            } catch (IllegalArgumentException e2) {
                return null;
            }
        }
    }
}
