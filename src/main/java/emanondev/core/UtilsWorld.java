package emanondev.core;

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

public final class UtilsWorld {

    private UtilsWorld() {

    }

    /**
     * @param name               world name
     * @param generator          generator
     * @param type               Set null for Empty world (Void)
     * @param generateStructures should generate structures like villages and caves?
     * @param seed               world seed, random if 0
     * @return true if successfully created
     */
    public static boolean create(String name, Environment generator, WorldType type, boolean generateStructures, long seed) {
        if (Bukkit.getWorld(name) == null) {
            WorldCreator c = new WorldCreator(name);
            c.generateStructures(generateStructures);
            if (generator != null)
                c.environment(generator);
            if (seed != 0)
                c.seed(seed);
            if (type != null)
                c.type(type);
            else
                c.generator(new VoidWorldGenerator());
            World world = Bukkit.getServer().createWorld(c);
            if (type == null) {
                world.getSpawnLocation().setX(0);
                int y = world.getSpawnLocation().getBlock().getY();
                world.getSpawnLocation().setZ(0);
                Location loc = new Location(world, 0, y - 1, 0);
                loc.getBlock().setType(Material.GLASS);
            }
            return true;
        }
        return false;
    }

    /**
     * also delete any file inside world folder
     *
     * @param world    world to delete
     * @param fallback fallback location for players
     * @return true if world has been successfully deleted
     */
    public static boolean delete(World world, @Nullable Location fallback) {
        if (!unload(world, false, fallback))
            return false;
        try {
            deleteDirectoryRecursion(world.getWorldFolder().toPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
    }

    private static Location getFallbackLocation(Location l, World w) {
        if (l == null)
            return Bukkit.getWorlds().get(0).equals(w) ? Bukkit.getWorlds().get(1).getSpawnLocation()
                    : Bukkit.getWorlds().get(0).getSpawnLocation();

        if (l.getWorld().equals(w) || !l.isWorldLoaded()) {
            l = Bukkit.getWorlds().get(0).equals(w) ? Bukkit.getWorlds().get(1).getSpawnLocation()
                    : Bukkit.getWorlds().get(0).getSpawnLocation();
            new IllegalArgumentException("invalid input location correcting it with " + l).printStackTrace();
        }
        return l;
    }

    /**
     * @param name      world name
     * @param saveWorld should save chunks?
     * @param fallback  fallback location for players
     * @return true if unloaded
     * @see #unload(World, boolean, Location)
     */
    public static boolean unload(String name, boolean saveWorld, @Nullable Location fallback) {
        return unload(Bukkit.getWorld(name), saveWorld, fallback);
    }

    /**
     * @param world     the world
     * @param saveWorld should save chunks?
     * @param fallback  fallback location for players
     * @return true if unloaded
     */
    public static boolean unload(World world, boolean saveWorld, @Nullable Location fallback) {
        if (world == null)
            return false;
        List<World> list = Bukkit.getWorlds();
        fallback = getFallbackLocation(fallback, world);
        for (Player p : world.getPlayers())
            p.teleport(fallback);
        boolean result = Bukkit.unloadWorld(world, saveWorld);
        list.remove(world);
        return result;

    }
}
