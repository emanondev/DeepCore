package emanondev.core;

import org.bukkit.Bukkit;

/**
 * @see emanondev.core.util.GameVersion
 * @deprecated replaced
 */
@Deprecated
public enum MCVersion {
    UNKNOWN("unknown", 100), V1_18("1.18", 16), V1_17("1.17", 15), V1_16_4("1.16.4", 14), V1_16_2("1.16.2", 13), V1_16("1.16", 12),
    V1_15("1.15", 11), V1_14("1.14", 10), V1_13_1("1.13.1", 9), V1_13("1.13", 8), V1_12("1.12", 7), V1_11("1.11", 6),
    V1_10("1.10", 5), V1_9_4("1.9.4", 4), V1_9("1.9", 3), V1_8_4("1.8.4", 2), V1_8_3("1.8.3", 1), V1_8("1.8", 0),
    V1_7("1.7", -1);

    private static final MCVersion version = fromPackageName(Bukkit.getServer().getClass().getPackage().getName());
    private final String name;
    private final int shortNum;

    MCVersion(String name, int shortNum) {
        this.name = name;
        this.shortNum = shortNum;
    }

    public static MCVersion getCurrentVersion() {
        return version;
    }

    private static MCVersion fromPackageName(String packageName) {
        if (packageName.contains("1_18_R1"))
            return V1_18;
        if (packageName.contains("1_17_R1"))
            return V1_17;
        if (packageName.contains("1_16_R3"))
            return V1_16_4;
        if (packageName.contains("1_16_R2"))
            return V1_16_2;
        if (packageName.contains("1_16_R1"))
            return V1_16;
        if (packageName.contains("1_15_R1"))
            return V1_15;
        if (packageName.contains("1_14_R1"))
            return V1_14;
        if (packageName.contains("1_13_R2"))
            return V1_13_1;
        if (packageName.contains("1_13_R1"))
            return V1_13;
        if (packageName.contains("1_12_R1"))
            return V1_12;
        if (packageName.contains("1_11_R1"))
            return V1_11;
        if (packageName.contains("1_10_R1"))
            return V1_10;
        if (packageName.contains("1_9_R2"))
            return V1_9_4;
        if (packageName.contains("1_9_R1"))
            return V1_9;
        if (packageName.contains("1_8_R3"))
            return V1_8_4;
        if (packageName.contains("1_8_R2"))
            return V1_8_3;
        if (packageName.contains("1_8_R1"))
            return V1_8;
        if (packageName.contains("1_7_"))
            return V1_7;
        return UNKNOWN;
    }

    public static boolean isCraftBukkit() {
        return !hasSpigotAPI();
    }

    public static boolean hasSpigotAPI() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    public static boolean hasPaperAPI() {
        try {
            Class.forName("com.destroystokyo.paper.VersionHistoryManager.VersionData");
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    public String toString() {
        return this.name;
    }

    public int getNumber() {
        return this.shortNum;
    }

    public boolean isOlderThan(MCVersion version) {
        return (compareWith(version) < 0);
    }

    public int compareWith(MCVersion version) {
        return this.shortNum - version.shortNum;
    }

    public int getMetaVersion() {
        if (this.isNewerOrEqualTo(V1_17))
            return 4;
        if (this.isNewerOrEqualTo(V1_15))
            return 3;
        if (this.isNewerOrEqualTo(V1_14))
            return 2;
        if (this.isNewerOrEqualTo(V1_13))
            return 1;
        if (this.isNewerOrEqualTo(V1_11))
            return 0;
        return -1;
    }

    public boolean isNewerOrEqualTo(MCVersion version) {
        return (compareWith(version) >= 0);
    }

    public boolean isNewerThan(MCVersion version) {
        return (compareWith(version) > 0);
    }

    public boolean isBetweenInclusively(MCVersion v1, MCVersion v2) {
        int difference = v1.compareWith(v2);
        if (difference == 0)
            return equals(v1);
        if (difference < 0)
            return (isNewerOrEqualTo(v1) && isOlderOrEqualTo(v2));
        return (isNewerOrEqualTo(v2) && isOlderOrEqualTo(v1));
    }

    public boolean isOlderOrEqualTo(MCVersion version) {
        return (compareWith(version) <= 0);
    }

    public boolean isLegacy() {
        return isOlderOrEqualTo(V1_12);
    }

    public boolean isOld() {
        return isOlderOrEqualTo(V1_8_4);
    }

}
