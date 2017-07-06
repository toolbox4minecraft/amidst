package amidst.mojangapi.world.oracle;

import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Coordinates;

@ThreadSafe
public class HeuristicWorldSpawnOracle implements WorldSpawnOracle {
	private final long seed;
	private final BiomeDataOracle biomeDataOracle;
	private final List<Biome> validBiomes;

	public HeuristicWorldSpawnOracle(long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomes) {
		this.seed = seed;
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomes = validBiomes;
	}

	@Override
	public Coordinates get() {
		return biomeDataOracle.findValidLocation(0, 0, 256, validBiomes, new Random(seed));
	}
}
