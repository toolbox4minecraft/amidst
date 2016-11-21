package amidst.mojangapi.world.icon.producer;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.oracle.WorldSpawnOracle;

@ThreadSafe
public class SpawnProducer extends CachedWorldIconProducer {
	private final WorldSpawnOracle oracle;

	public SpawnProducer(WorldSpawnOracle oracle) {
		this.oracle = oracle;
	}

	@Override
	protected List<WorldIcon> doCreateCache() {
		return Arrays.asList(createSpawnWorldIcon());
	}

	private WorldIcon createSpawnWorldIcon() {
		CoordinatesInWorld spawnLocation = oracle.get();
		if (spawnLocation != null) {
			return createWorldIcon(spawnLocation);
		} else {
			CoordinatesInWorld origin = CoordinatesInWorld.origin();
			AmidstLogger.info("Unable to find spawn biome. Falling back to " + origin.toString() + ".");
			return createWorldIcon(origin);
		}
	}

	private WorldIcon createWorldIcon(CoordinatesInWorld coordinates) {
		return new WorldIcon(
				coordinates,
				DefaultWorldIconTypes.SPAWN.getLabel(),
				DefaultWorldIconTypes.SPAWN.getImage(),
				Dimension.OVERWORLD,
				false);
	}
}
