package amidst.mojangapi.world.oracle;

import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@ThreadSafe
public class HeuristicWorldSpawnOracle implements WorldSpawnOracle {
	private final long seed;
	private final BiomeDataOracle biomeDataOracle;
	private final List<Integer> validBiomeIds;

	public HeuristicWorldSpawnOracle(long seed, BiomeDataOracle biomeDataOracle, List<Integer> validBiomeIds) {
		this.seed = seed;
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomeIds = validBiomeIds;
	}

	@Override
	public CoordinatesInWorld get() {
		return biomeDataOracle.findValidLocation(0, 0, 256, validBiomeIds, new Random(seed));
	}
}
