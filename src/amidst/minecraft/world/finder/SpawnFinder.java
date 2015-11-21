package amidst.minecraft.world.finder;

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

public class SpawnFinder extends CachedFinder {
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

	public SpawnFinder(World world) {
		super(world);
	}

	@Override
	protected List<Finding> createCache() {
		Finding spawn = createSpawnFinding();
		if (spawn != null) {
			return Arrays.asList(spawn);
		} else {
			Log.debug("Unable to find spawn biome.");
			return null;
		}
	}

	private Finding createSpawnFinding() {
		Point spawnCenter = getSpawnCenterInWorldCoordinates();
		if (spawnCenter != null) {
			return new Finding(CoordinatesInWorld.from(spawnCenter.x,
					spawnCenter.y), MapMarkers.SPAWN.getName(),
					MapMarkers.SPAWN.getImage());
		} else {
			return null;
		}
	}

	private Point getSpawnCenterInWorldCoordinates() {
		Random random = new Random(world.getSeed());
		return MinecraftUtil.findValidLocation(0, 0, 256, VALID_BIOMES, random);
	}
}
