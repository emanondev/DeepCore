package emanondev.core;

import emanondev.virginblock.VirginBlockAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class Hooks {
    @Deprecated
    public static boolean isVault() {
        return isEnabled("Vault");
    }

    public static boolean isEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    public static boolean isVaultEnabled() {
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

    public static boolean isAnyWorldEditEnabled() {
        return isEnabled("WorldEdit") || isWorldEditAsync();
    }

    public static boolean isWorldEditAsync() {
        return isFAWEEnabled() || isEnabled("AsyncWorldEdit");
    }

    public static boolean isFAWEEnabled() {
        return isEnabled("FastAsyncWorldEdit");
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
}
