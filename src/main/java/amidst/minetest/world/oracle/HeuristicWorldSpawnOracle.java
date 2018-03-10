package amidst.minetest.world.oracle;

import amidst.documentation.ThreadSafe;
import amidst.minetest.world.mapgen.MapgenV7Params;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.oracle.WorldSpawnOracle;

@ThreadSafe
public class HeuristicWorldSpawnOracle implements WorldSpawnOracle {
	private final int seed;
	private final MapgenV7Params params;

	public HeuristicWorldSpawnOracle(MapgenV7Params params, long seed) {
		this.seed = (int)(seed & 0xFFFFFFFFL);
		this.params = params;
	}

	@Override
	public CoordinatesInWorld get() {
		// Not implemented
		return CoordinatesInWorld.origin();
	}
}
