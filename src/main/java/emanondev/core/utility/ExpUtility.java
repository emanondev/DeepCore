package emanondev.core.utility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for handling experience (EXP) operations in Minecraft.
 * Provides methods to modify, calculate, and check player experience.
 */
public final class ExpUtility {

    private ExpUtility() {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes a specified amount of experience from the player.
     * If the player's experience falls below zero, it is set to zero.
     *
     * @param player The player whose experience is to be reduced.
     * @param exp    The amount of experience to remove.
     */
    public static void removeExp(final @NotNull Player player, final int exp) {
        setExp(player, Math.max(0, getExp(player) - exp));
    }

    /**
     * Sets the player's total experience to a specific value.
     * The level and experience progress are adjusted accordingly.
     *
     * @param player The player whose experience is being set.
     * @param exp    The new total experience value. Must be non-negative.
     * @throws IllegalArgumentException If {@code exp} is negative.
     */
    public static void setExp(final @NotNull Player player, final int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("exp must be positive");
        }
        int level = getLevelAtExp(exp);
        player.setExp(0);
        player.setLevel(level);
        player.giveExp(exp - getExpAtLevel(level));
    }

    /**
     * Calculates the player's current total experience.
     *
     * @param player The player whose experience is being calculated.
     * @return The total experience points of the player.
     */
    public static int getExp(final @NotNull Player player) {
        int level = player.getLevel();
        int exp = getExpAtLevel(level);
        exp += Math.round(getExpToLevelUp(level) * player.getExp());
        return exp;
    }

    /**
     * Determines the player level corresponding to a given total experience amount.
     *
     * @param exp The total experience.
     * @return The level corresponding to the given experience.
     */
    public static int getLevelAtExp(final int exp) {
        int expValue = exp;
        int level = 1;
        while (expValue > 0) {
            expValue -= getExpToLevelUp(level);
            level++;
        }
        return level - 1;
    }

    /**
     * Calculates the total experience required to reach a specific level.
     *
     * @param level The target level.
     * @return The total experience needed to reach the given level.
     * @throws IllegalArgumentException If {@code level} is negative.
     */
    public static int getExpAtLevel(final int level) {
        if (level < 0) {
            throw new IllegalArgumentException("level must be positive");
        }
        if (level == 0) return 0;
        if (level <= 16) return (int) (Math.pow(level, 2) + 6 * level);
        else if (level <= 31) return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
        else return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
    }

    /**
     * Determines the experience required to level up from a given level.
     *
     * @param level The current level.
     * @return The experience required to reach the next level.
     * @throws IllegalArgumentException If {@code level} is negative.
     */
    public static int getExpToLevelUp(final int level) {
        if (level < 0) {
            throw new IllegalArgumentException("level must be positive");
        }
        if (level <= 15) return 2 * level + 7;
        else if (level <= 30) return 5 * level - 38;
        else return 9 * level - 158;
    }

    /**
     * Checks whether a player has at least a certain amount of experience.
     *
     * @param player The player to check.
     * @param exp    The minimum required experience.
     * @return {@code true} if the player has at least {@code exp} experience, otherwise {@code false}.
     * @throws IllegalArgumentException If {@code exp} is negative.
     */
    public static boolean hasExp(final @NotNull Player player, final int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("exp must be positive");
        }
        return getExp(player) >= exp;
    }
}
