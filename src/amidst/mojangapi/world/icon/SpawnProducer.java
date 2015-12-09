package amidst.mojangapi.world.icon;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.Biome;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

public class SpawnProducer extends CachedWorldIconProducer {
	// @formatter:off
	private static final List<Biome> VALID_BIOMES = Arrays.asList(
			Biome.forest,
			Biome.plains,
			Biome.taiga,
			Biome.taigaHills,
			Biome.forestHills,
			Biome.jungle,
			Biome.jungleHills
	);
	// @formatter:on

	private final long seed;
	private final BiomeDataOracle biomeDataOracle;

	public SpawnProducer(RecognisedVersion recognisedVersion, long seed,
			BiomeDataOracle biomeDataOracle) {
		super(recognisedVersion);
		this.seed = seed;
		this.biomeDataOracle = biomeDataOracle;
	}

	@Override
	protected List<WorldIcon> createCache() {
		return Arrays.asList(createSpawnWorldIcon());
	}

	private WorldIcon createSpawnWorldIcon() {
		Point spawnCenter = getSpawnCenterInWorldCoordinates();
		if (spawnCenter != null) {
			return createSpawnWorldIconAt(CoordinatesInWorld.from(
					spawnCenter.x, spawnCenter.y));
		} else {
			Log.debug("Unable to find spawn biome.");
			return createSpawnWorldIconAt(CoordinatesInWorld.origin());
		}
	}

	private WorldIcon createSpawnWorldIconAt(CoordinatesInWorld coordinates) {
		return new WorldIcon(coordinates,
				DefaultWorldIconTypes.SPAWN.getName(),
				DefaultWorldIconTypes.SPAWN.getImage());
	}

	private Point getSpawnCenterInWorldCoordinates() {
		Random random = new Random(seed);
		return biomeDataOracle.findValidLocation(0, 0, 256, VALID_BIOMES,
				random);
	}
}
