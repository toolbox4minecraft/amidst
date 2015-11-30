package amidst.minecraft.world.object;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.logging.Log;
import amidst.map.MapMarkers;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;

public class SpawnProducer extends CachedWorldObjectProducer {
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
	protected List<WorldObject> createCache() {
		return Arrays.asList(createSpawnWorldObject());
	}

	private WorldObject createSpawnWorldObject() {
		Point spawnCenter = getSpawnCenterInWorldCoordinates();
		if (spawnCenter != null) {
			return new WorldObject(CoordinatesInWorld.from(spawnCenter.x,
					spawnCenter.y), MapMarkers.SPAWN);
		} else {
			Log.debug("Unable to find spawn biome.");
			return new WorldObject(CoordinatesInWorld.origin(),
					MapMarkers.SPAWN);
		}
	}

	private Point getSpawnCenterInWorldCoordinates() {
		Random random = new Random(world.getSeed());
		return MinecraftUtil.findValidLocation(0, 0, 256, VALID_BIOMES, random);
	}
}
