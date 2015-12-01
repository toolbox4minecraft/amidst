package amidst.minecraft.world.icon;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.logging.Log;
import amidst.map.DefaultWorldIconTypes;
import amidst.minecraft.Biome;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;

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

	public SpawnProducer(World world) {
		super(world);
	}

	@Override
	protected List<WorldIcon> createCache() {
		return Arrays.asList(createSpawnWorldIcon());
	}

	private WorldIcon createSpawnWorldIcon() {
		Point spawnCenter = getSpawnCenterInWorldCoordinates();
		if (spawnCenter != null) {
			return new WorldIcon(CoordinatesInWorld.from(spawnCenter.x,
					spawnCenter.y), DefaultWorldIconTypes.SPAWN);
		} else {
			Log.debug("Unable to find spawn biome.");
			return new WorldIcon(CoordinatesInWorld.origin(), DefaultWorldIconTypes.SPAWN);
		}
	}

	private Point getSpawnCenterInWorldCoordinates() {
		Random random = new Random(world.getSeed());
		return world.getBiomeDataOracle().findValidLocation(0, 0, 256,
				VALID_BIOMES, random);
	}
}
