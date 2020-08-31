package amidst.mojangapi.world.oracle;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.util.FastRand;

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
	public CoordinatesInWorld get() {
		return biomeDataOracle.findValidLocation(0, 0, 256, validBiomes, new FastRand(seed));
	}
}
