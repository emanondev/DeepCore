package emanondev.core;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * PaperLib is shaded inside the plugin, you can use it here instead of shading on every plugin where it is required
 */
public class PaperLib {

    private PaperLib() {
        throw new AssertionError();
    }

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
     * @return Future that completes with the chunk, or null if the chunk did not exist and generation was not requested.
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
     * @return Future that completes with the chunk, or null if the chunk did not exist and generation was not requested.
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
     * @return Future that completes with the chunk, or null if the chunk did not exist and generation was not requested.
     */
    @NotNull
    public static CompletableFuture<Chunk> getChunkAtAsync(@NotNull World world, int x, int z, boolean gen, boolean isUrgent) {
        return io.papermc.lib.PaperLib.getChunkAtAsync(world, x, z, gen, isUrgent);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed, with the highest priority if supported
     *
     * @param world World to load chunk for
     * @param x     X coordinate of the chunk to load
     * @param z     Z coordinate of the chunk to load
     * @param gen   Should the chunk generate or not. Only respected on some MC versions, 1.13 for CB, 1.12 for Paper
     * @return Future that completes with the chunk, or null if the chunk did not exist and generation was not requested.
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
     * @param isUrgent Whether this should be performed with the highest priority when supported
     * @return Future that completes with the location of the bed spawn location, or null if the player
     * has not slept in a bed or if the bed spawn is invalid.
     */
    public static CompletableFuture<Location> getBedSpawnLocationAsync(@NotNull Player player, boolean isUrgent) {
        return io.papermc.lib.PaperLib.getBedSpawnLocationAsync(player, isUrgent);
    }

}
