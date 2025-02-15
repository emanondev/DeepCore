package emanondev.core.util;

import org.bukkit.Bukkit;

/**
 * @see emanondev.core.utility.VersionUtility
 */
@Deprecated
public final class GameVersion {

    public static final String NMS_VERSION = loadNmsver();
    public static final int GAME_MAIN_VERSION = Integer.parseInt(NMS_VERSION.split("_")[0].substring(1));
    public static final int GAME_VERSION = Integer.parseInt(NMS_VERSION.split("_")[1]);
    public static final int GAME_SUB_VERSION = Integer.parseInt(NMS_VERSION.split("_")[2].substring(1));

    private GameVersion() {
        throw new AssertionError();
    }

    private static String loadNmsver() {
        String txt = Bukkit.getServer().getClass().getPackage().getName();
        return txt.substring(txt.lastIndexOf(".") + 1);
    }

    public static boolean hasSpigot() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return true;
        } catch (ClassNotFoundException e1) {
            return false;
        }
    }

    public static boolean hasPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException e1) {
            return false;
        }
    }

    public static boolean isNewerEqualsTo(int mainVersion, int version, int subVersion) {
        return !isOlderThan(mainVersion, version, subVersion);
    }

    public static boolean isOlderThan(int mainVersion, int version, int subVersion) {
        if (GAME_MAIN_VERSION > mainVersion)
            return false;
        if (GAME_MAIN_VERSION < mainVersion)
            return true;
        if (GAME_VERSION > version)
            return false;
        if (GAME_VERSION < version)
            return true;
        if (GAME_SUB_VERSION > subVersion)
            return false;
        return GAME_SUB_VERSION < subVersion;
    }
}
