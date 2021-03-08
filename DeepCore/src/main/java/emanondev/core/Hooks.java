package emanondev.core;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import emanondev.virginblock.VirginBlockAPI;

public class Hooks {
	public static boolean isVault() {
		return Bukkit.getPluginManager().isPluginEnabled("Vault");
	}
	public static boolean isVanishEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("SuperVanish")||Bukkit.getPluginManager().isPluginEnabled("PremiumVanish");
	}
	public static boolean isPAPIEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
	}
	public static boolean isMcmmoEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Mcmmo");
	}
	public static boolean isBlockVirgin(Block block) {
		if (!Bukkit.getPluginManager().isPluginEnabled("VirginBlock"))
			return true;
		else
			return VirginBlockAPI.isBlockVirgin(block);
	}
	public static boolean isCitizenEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Citizens");
	}
	public static boolean isVirginBlockPluginEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("VirginBlock");
	}
	public static boolean isRegionAPIEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("WorldGuard")&&
				Bukkit.getPluginManager().isPluginEnabled("WorldGuardRegionAPI");
	}
	public static boolean isJobsEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Jobs");
	}
	public static boolean isMythicMobsEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
	}
	public static boolean isSkillAPIEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("SkillAPI");
	}
	public static boolean isTownyEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Towny");
	}
	public static boolean isPartiesEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Parties");
	}
}
