package amidst.mojangapi.world.oracle;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.Coordinates;

@ThreadSafe
public interface WorldSpawnOracle {
	Coordinates get();
}
