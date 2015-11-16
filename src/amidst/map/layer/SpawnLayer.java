package amidst.map.layer;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class SpawnLayer extends IconLayer {
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

	private MapObject spawn;

	@Override
	public boolean isVisible() {
		return Options.instance.showSpawn.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		if (spawn != null && fragment.isInBounds(spawn)) {
			spawn.setFragment(fragment);
		}
	}

	@Override
	public void reload() {
		initSpawnObject();
	}

	private void initSpawnObject() {
		Point spawnCenter = getSpawnCenterInWorldCoordinates();
		if (spawnCenter != null) {
			spawn = MapObject.fromWorldCoordinates(this, MapMarkers.SPAWN,
					spawnCenter.x, spawnCenter.y);
		} else {
			Log.debug("Unable to find spawn biome.");
			spawn = null;
		}
	}

	private Point getSpawnCenterInWorldCoordinates() {
		Random random = new Random(Options.instance.world.getSeed());
		return MinecraftUtil.findValidLocation(0, 0, 256, VALID_BIOMES, random);
	}
}
