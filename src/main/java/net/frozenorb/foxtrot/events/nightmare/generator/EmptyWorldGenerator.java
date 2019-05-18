package net.frozenorb.foxtrot.events.nightmare.generator;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class EmptyWorldGenerator extends ChunkGenerator {

	@Override
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biomeGrid) {
		return new byte[world.getMaxHeight() / 16][];
	}

	public Location getFixedSpawnLocation(final World world, final Random random) {
		return new Location(world, 0, 65, 0);
	}

}

