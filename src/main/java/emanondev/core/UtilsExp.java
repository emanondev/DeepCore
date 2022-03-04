package emanondev.core;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

public class UtilsExp {
	
	UtilsExp(){
		throw new UnsupportedOperationException();
	}

	// Calculate amount of EXP needed to level up
	public static int getExpToLevelUp(int level) {
		Validate.isTrue(level >= 0,"level must be positive");
		// 1.8+
		if (level <= 15)
			return 2 * level + 7;
		else if (level <= 30)
			return 5 * level - 38;
		else
			return 9 * level - 158;
	}

	// Calculate total experience up to a level
	public static int getExpAtLevel(int level) {
		Validate.isTrue(level >= 0,"level must be positive");
		// 1.8+
		if (level==0)
			return 0;
		if (level <= 16)
			return (int) (Math.pow(level, 2) + 6 * level);
		else if (level <= 31)
			return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
		else
			return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
	}

	public static int getLevelAtExp(int exp) {
		int level = 1;
		while (exp > 0) {
			exp -= getExpToLevelUp(level);
			level++;
		}
		return level - 1;
	}

	// Calculate player's current EXP amount
	public static int getExp(Player player) {
		Validate.notNull(player,"player is null");
		int level = player.getLevel();

		// Get the amount of XP in past levels
		int exp = getExpAtLevel(level);

		// Get amount of XP towards next level
		exp += Math.round(getExpToLevelUp(level) * player.getExp());

		return exp;
	}

	// Give or take EXP
	public static void setExp(Player player, int exp) {
		Validate.isTrue(exp >= 0,"exp must be positive");
		int level = getLevelAtExp(exp);
		// Reset player's current exp to 0
		player.setExp(0);
		player.setLevel(level);
		player.giveExp(exp - getExpAtLevel(level));
	}

	public static void removeExp(Player player, int exp) {
		setExp(player, Math.max(0, getExp(player) - exp));
	}

	public static boolean hasExp(Player player, int exp) {
		Validate.isTrue(exp >= 0,"exp must be positive");
		return getExp(player) >= exp;
	}

}
