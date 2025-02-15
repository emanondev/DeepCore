package emanondev.core.utility;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReadHelper {

    /**
     * @param arg argument to read
     * @return target player or null if player is offline or vanished
     * @see #readPlayer(CommandSender, String)
     */
    @Deprecated
    @Nullable
    default Player readPlayer(final @NotNull String arg) {
        return ReadUtility.readPlayer(null, arg);
    }

    /**
     * @param sender who sended the command
     * @param arg    argument to read
     * @return target player or null if player is offline or vanished
     */
    @Nullable
    default Player readPlayer(final CommandSender sender, final @NotNull String arg) {
        return ReadUtility.readPlayer(sender, arg);
    }

    /**
     * @param arg argument to read
     * @return target player or null if player never joined the server
     */
    @Nullable
    default OfflinePlayer readOfflinePlayer(final @NotNull String arg) {
        return ReadUtility.readOfflinePlayer(arg);
    }

    /**
     * @param arg argument to read
     * @return int value of the string or null if parsing failed
     */
    @Nullable
    default Integer readInt(final @NotNull String arg) {
        return ReadUtility.readInt(arg);
    }

    /**
     * @param arg argument to read
     * @return boolean value of the string or null if parsing failed
     */
    @Nullable
    default Boolean readBoolean(final @NotNull String arg) {
        return ReadUtility.readBoolean(arg);
    }

    /**
     * @param arg argument to read
     * @return double value of the string or null if parsing failed
     */
    @Nullable
    default Double readDouble(final @NotNull String arg) {
        return ReadUtility.readDouble(arg);
    }


    @Nullable
    default <T extends Enum<T>> T readEnum(final @NotNull String arg,
                                           final @NotNull Class<T> clazz) {
        return ReadUtility.readEnum(arg, clazz);
    }
}
