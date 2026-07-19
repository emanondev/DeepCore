package emanondev.core.utility;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;


/**
 * Utility class for managing server version information and compatibility checks.
 */
@Slf4j
public final class VersionUtility {
    private static int initVersion(int slot){
        String[] split;
        try {
            split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        } catch (Exception e) {
            log.warn("(slot {}) Invalid Bukkit version format: {}", slot, Bukkit.getBukkitVersion(), e);
            return 100; //avoid stopping the load, try to start with wrong version
        }
        try {
            if (split.length<=slot) {
                return 0;
            }
            if ("build".equals(split[slot])) {
                return 0;
            }
            return Integer.parseInt(split[slot]);
        } catch (Exception e){
            log.warn("(slot {}) Invalid Bukkit version format: {}", slot, Bukkit.getBukkitVersion(), e);
            return 100; //avoid stopping the load, try to start with wrong version
        }
    }

    /**
     * The major version of the game (e.g., the first number in the version string).
     */
    public static final int GAME_MAIN_VERSION = initVersion(0);

    /**
     * The minor version of the game (e.g., the second number in the version string).
     */
    public static final int GAME_VERSION = initVersion(1);

    /**
     * The patch version of the game, or 0 if the version string has fewer than three parts.
     */
    public static final int GAME_SUB_VERSION = initVersion(2);

    private static final boolean HAS_PAPER = ReflectionUtility
            .isClassPresent("com.destroystokyo.paper.VersionHistoryManager$VersionData");
    private static final boolean HAS_FOLIA = ReflectionUtility
            .isClassPresent("io.papermc.paper.threadedregions.RegionizedServer");
    private static final boolean HAS_PURPUR = ReflectionUtility
            .isClassPresent("org.purpurmc.purpur.event.PlayerAFKEvent");

    private VersionUtility() {
        throw new UnsupportedOperationException("VersionUtils is a utility class and cannot be instantiated.");
    }

    /**
     * Determines the type of server (e.g., Folia, Purpur, Paper, or Spigot).
     *
     * @return the type of server as a string.
     */
    @NotNull
    public static String getVersionType() {
        return hasFoliaAPI() ? "Folia" :
                (hasPurpurAPI() ? "Purpur" :
                 (hasPaperAPI() ? "Paper" : "Spigot"));
    }

    /**
     * Constructs the full version number as a string in the format "main.minor.sub".
     *
     * @return the version number as a string.
     */
    @NotNull
    public static String getVersionNumber() {
        return GAME_MAIN_VERSION + "." + GAME_VERSION + "." + GAME_SUB_VERSION;
    }

    /**
     * Constructs the full server version string, including type and version number.
     *
     * @return the full server version as a string.
     */
    @NotNull
    public static String getVersion() {
        return getVersionType() + " " + getVersionNumber();
    }

    /**
     * Checks if the current version is up to (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param mainVersion the main version.
     * @param version     the minor version.
     * @param subVersion  the sub version.
     * @return true if the current version is up to the specified version.
     */
    public static boolean isOlderEquals(final int mainVersion,
                                        final int version,
                                        final int subVersion) {
        if (GAME_MAIN_VERSION > mainVersion) return false;
        if (GAME_MAIN_VERSION < mainVersion) return true;
        if (GAME_VERSION > version) return false;
        if (GAME_VERSION < version) return true;
        return GAME_SUB_VERSION <= subVersion;
    }

    /**
     * Checks if the current version is after (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param mainVersion the main version.
     * @param version     the minor version.
     * @param subVersion  the sub version.
     * @return true if the current version is after the specified version.
     */
    public static boolean isNewerEquals(final int mainVersion,
                                        final int version,
                                        final int subVersion) {
        if (GAME_MAIN_VERSION < mainVersion) return false;
        if (GAME_MAIN_VERSION > mainVersion) return true;
        if (GAME_VERSION < version) return false;
        if (GAME_VERSION > version) return true;
        return GAME_SUB_VERSION >= subVersion;
    }

    /**
     * Checks if the current version is within the specified range.
     * Inclusive comparison.
     *
     * @param mainVersionMin the minimum main version.
     * @param versionMin     the minimum minor version.
     * @param subVersionMin  the minimum sub version.
     * @param mainVersionMax the maximum main version.
     * @param versionMax     the maximum minor version.
     * @param subVersionMax  the maximum sub version.
     * @return true if the current version is within the range.
     */
    public static boolean isInRange(final int mainVersionMin,
                                    final int versionMin,
                                    final int subVersionMin,
                                    final int mainVersionMax,
                                    final int versionMax,
                                    final int subVersionMax) {
        return isNewerEquals(mainVersionMin, versionMin, subVersionMin) &&
                isOlderEquals(mainVersionMax, versionMax, subVersionMax);
    }

    /**
     * Checks if the Paper API is available.
     *
     * @return true if Paper API is present, false otherwise.
     */
    public static boolean hasPaperAPI() {
        return HAS_PAPER;
    }

    /**
     * Checks if the Purpur API is available.
     *
     * @return true if Purpur API is present, false otherwise.
     */
    public static boolean hasPurpurAPI() {
        return HAS_PURPUR;
    }

    /**
     * Checks if the Folia API is available.
     *
     * @return true if Folia API is present, false otherwise.
     */
    public static boolean hasFoliaAPI() {
        return HAS_FOLIA;
    }

    private static String[] safeSplitVersion() {
        try {
            return Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        } catch (Exception e) {
            throw new IllegalStateException("Invalid Bukkit version format: " + Bukkit.getBukkitVersion(), e);
        }
    }
}