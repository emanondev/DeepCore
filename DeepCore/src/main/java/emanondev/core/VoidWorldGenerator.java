package emanondev.core;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class VoidWorldGenerator extends ChunkGenerator {

	private final List<BlockPopulator> blockPopulator = Arrays.asList(new BlockPopulator[0]);
	private final byte[] bytes = new byte[32768];

	public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int x, int z,
			ChunkGenerator.BiomeGrid biome) {
		ChunkGenerator.ChunkData chunkData = createChunkData(world);
		chunkData.setRegion(0, 0, 0, 16, 2, 16, Material.AIR);

		return chunkData;
	}

	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
		return this.bytes;
	}

	public List<BlockPopulator> getDefaultPopulators(World world) {
		return this.blockPopulator;
	}

	public Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, 0, 128, 0);
	}

}