package emanondev.core;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class VoidWorldGenerator extends ChunkGenerator {

    private final List<BlockPopulator> blockPopulator = List.of();
    private final byte[] bytes = new byte[32768];

    public ChunkGenerator.@NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z,
                                                               ChunkGenerator.BiomeGrid biome) {
        ChunkGenerator.ChunkData chunkData = createChunkData(world);
        chunkData.setRegion(0, 0, 0, 16, 2, 16, Material.AIR);

        return chunkData;
    }

    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return this.blockPopulator;
    }

    public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0, 128, 0);
    }

    public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
        return this.bytes;
    }

}