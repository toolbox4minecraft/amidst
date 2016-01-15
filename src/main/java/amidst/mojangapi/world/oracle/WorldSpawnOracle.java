package amidst.mojangapi.world.oracle;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@ThreadSafe
public interface WorldSpawnOracle {
	CoordinatesInWorld get();
}
