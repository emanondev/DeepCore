package emanondev.core;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

public class PaperLib {

    /**
     * Teleports an Entity to the target location, loading the chunk asynchronously first if needed.
     *
     * @param entity   The Entity to teleport
     * @param location The Location to Teleport to
     * @return Future that completes with the result of the teleport
     */
    @NotNull
    public static CompletableFuture<Boolean> teleportAsync(@NotNull Entity entity, @NotNull Location location) {
        return io.papermc.lib.PaperLib.teleportAsync(entity, location, TeleportCause.PLUGIN);
    }

    /**
     * Teleports an Entity to the target location, loading the chunk asynchronously first if needed.
     *
     * @param entity   The Entity to teleport
     * @param location The Location to Teleport to
     * @param cause    The cause for the teleportation
     * @return Future that completes with the result of the teleport
     */
    @NotNull
    public static CompletableFuture<Boolean> teleportAsync(@NotNull Entity entity, @NotNull Location location, TeleportCause cause) {
        return io.papermc.lib.PaperLib.teleportAsync(entity, location, cause);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param loc Location to get chunk for
     * @return Future that completes with the chunk
     */
    @NotNull
    public static CompletableFuture<Chunk> getChunkAtAsync(@NotNull Location loc) {
        return getChunkAtAsync(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4, true);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param loc Location to get chunk for
     * @param gen Should the chunk generate or not. Only respected on some MC versions, 1.13 for CB, 1.12 for Paper
     * @return Future that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    @NotNull
    public static CompletableFuture<Chunk> getChunkAtAsync(@NotNull Location loc, boolean gen) {
        return getChunkAtAsync(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4, gen);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param world World to load chunk for
     * @param x     X coordinate of the chunk to load
     * @param z     Z coordinate of the chunk to load
     * @return Future that completes with the chunk
     */
    @NotNull
    public static CompletableFuture<Chunk> getChunkAtAsync(@NotNull World world, int x, int z) {
        return getChunkAtAsync(world, x, z, true);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param world World to load chunk for
     * @param x     X coordinate of the chunk to load
     * @param z     Z coordinate of the chunk to load
     * @param gen   Should the chunk generate or not. Only respected on some MC versions, 1.13 for CB, 1.12 for Paper
     * @return Future that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    @NotNull
    public static CompletableFuture<Chunk> getChunkAtAsync(@NotNull World world, int x, int z, boolean gen) {
        return io.papermc.lib.PaperLib.getChunkAtAsync(world, x, z, gen, false);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param world World to load chunk for
     * @param x     X coordinate of the chunk to load
     * @param z     Z coordinate of the chunk to load
     * @param gen   Should the chunk generate or not. Only respected on some MC versions, 1.13 for CB, 1.12 for Paper
     * @return Future that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    @NotNull
    public static CompletableFuture<Chunk> getChunkAtAsync(@NotNull World world, int x, int z, boolean gen, boolean isUrgent) {
        return io.papermc.lib.PaperLib.getChunkAtAsync(world, x, z, gen, isUrgent);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed, with highest priority if supported
     *
     * @param world World to load chunk for
     * @param x     X coordinate of the chunk to load
     * @param z     Z coordinate of the chunk to load
     * @param gen   Should the chunk generate or not. Only respected on some MC versions, 1.13 for CB, 1.12 for Paper
     * @return Future that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    @NotNull
    public static CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NotNull World world, int x, int z, boolean gen) {
        return io.papermc.lib.PaperLib.getChunkAtAsync(world, x, z, gen, true);
    }

    /**
     * Checks if the chunk has been generated or not. Only works on Paper 1.12+ or any 1.13.1+ version
     *
     * @param loc Location to check if the chunk is generated
     * @return If the chunk is generated or not
     */
    public static boolean isChunkGenerated(@NotNull Location loc) {
        return isChunkGenerated(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    /**
     * Checks if the chunk has been generated or not. Only works on Paper 1.12+ or any 1.13.1+ version
     *
     * @param world World to check for
     * @param x     X coordinate of the chunk to check
     * @param z     Z coordinate of the chunk to check
     * @return If the chunk is generated or not
     */
    public static boolean isChunkGenerated(@NotNull World world, int x, int z) {
        return io.papermc.lib.PaperLib.isChunkGenerated(world, x, z);
    }

    /**
     * Gets the location where the target player will spawn at their bed, asynchronously if needed
     *
     * @param player   The player whose bed spawn location to get.
     * @param isUrgent Whether or not this should be performed with highest priority when supported
     * @return Future that completes with the location of the bed spawn location, or null if the player
     * has not slept in a bed or if the bed spawn is invalid.
     */
    public static CompletableFuture<Location> getBedSpawnLocationAsync(@NotNull Player player, boolean isUrgent) {
        return io.papermc.lib.PaperLib.getBedSpawnLocationAsync(player, isUrgent);
    }

    /**
     * Detects if the current MC version is at least the following version.
     * <p>
     * Assumes 0 patch version.
     *
     * @param minor Min Minor Version
     * @return Meets the version requested
     */
    public static boolean isVersion(int minor) {
        return io.papermc.lib.PaperLib.isVersion(minor);
    }

    /**
     * Detects if the current MC version is at least the following version.
     *
     * @param minor Min Minor Version
     * @param patch Min Patch Version
     * @return Meets the version requested
     */
    public static boolean isVersion(int minor, int patch) {
        return io.papermc.lib.PaperLib.isVersion(minor, patch);
    }

    /**
     * Gets the current Minecraft Minor version. IE: 1.13.1 returns 13
     *
     * @return The Minor Version
     */
    public static int getMinecraftVersion() {
        return io.papermc.lib.PaperLib.getMinecraftVersion();
    }

    /**
     * Gets the current Minecraft Patch version. IE: 1.13.1 returns 1
     *
     * @return The Patch Version
     */
    public static int getMinecraftPatchVersion() {
        return io.papermc.lib.PaperLib.getMinecraftPatchVersion();
    }

    /**
     * Gets the current Minecraft Pre-Release version if applicable, otherwise -1. IE: "1.14.3 Pre-Release 4" returns 4
     *
     * @return The Pre-Release Version if applicable, otherwise -1
     */
    public static int getMinecraftPreReleaseVersion() {
        return io.papermc.lib.PaperLib.getMinecraftPreReleaseVersion();
    }

    /**
     * Check if the server has access to the Spigot API
     *
     * @return True for Spigot <em>and</em> Paper environments
     */
    public static boolean isSpigot() {
        return io.papermc.lib.PaperLib.isSpigot();
    }

    /**
     * Check if the server has access to the Paper API
     *
     * @return True for Paper environments
     */
    public static boolean isPaper() {
        return io.papermc.lib.PaperLib.isPaper();
    }


}
