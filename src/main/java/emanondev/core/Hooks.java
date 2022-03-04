package emanondev.core;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import emanondev.virginblock.VirginBlockAPI;

public class Hooks {
    public static boolean isVault() {
        return isEnabled("Vault");
    }

    public static boolean isVanishEnabled() {
        return isEnabled("SuperVanish") || isEnabled("PremiumVanish");
    }

    public static boolean isPAPIEnabled() {
        return isEnabled("PlaceholderAPI");
    }

    public static boolean isMcmmoEnabled() {
        return isEnabled("Mcmmo");
    }

    public static boolean isBlockVirgin(Block block) {
        if (!isEnabled("VirginBlock"))
            return true;
        else
            return VirginBlockAPI.isBlockVirgin(block);
    }

    public static boolean isCitizenEnabled() {
        return isEnabled("Citizens");
    }

    public static boolean isVirginBlockPluginEnabled() {
        return isEnabled("VirginBlock");
    }

    public static boolean isRegionAPIEnabled() {
        return isEnabled("WorldGuard") &&
                isEnabled("WorldGuardRegionAPI");
    }

    public static boolean isJobsEnabled() {
        return isEnabled("Jobs");
    }

    public static boolean isMythicMobsEnabled() {
        return isEnabled("MythicMobs");
    }

    public static boolean isSkillAPIEnabled() {
        return isEnabled("SkillAPI");
    }

    public static boolean isTownyEnabled() {
        return isEnabled("Towny");
    }

    public static boolean isPartiesEnabled() {
        return isEnabled("Parties");
    }

    public static boolean isEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }
}
