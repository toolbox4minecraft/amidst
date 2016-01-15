package amidst.mojangapi.world.oracle;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@Immutable
public class ImmutableWorldSpawnOracle implements WorldSpawnOracle {
	private final CoordinatesInWorld worldSpawn;

	public ImmutableWorldSpawnOracle(CoordinatesInWorld worldSpawn) {
		this.worldSpawn = worldSpawn;
	}

	@Override
	public CoordinatesInWorld get() {
		return worldSpawn;
	}
}
