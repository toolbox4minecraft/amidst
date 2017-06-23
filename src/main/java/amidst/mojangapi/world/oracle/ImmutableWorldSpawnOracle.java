package amidst.mojangapi.world.oracle;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.Coordinates;

@Immutable
public class ImmutableWorldSpawnOracle implements WorldSpawnOracle {
	private final Coordinates worldSpawn;

	public ImmutableWorldSpawnOracle(Coordinates worldSpawn) {
		this.worldSpawn = worldSpawn;
	}

	@Override
	public Coordinates get() {
		return worldSpawn;
	}
}
