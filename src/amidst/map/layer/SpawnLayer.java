package amidst.map.layer;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.object.MapObjectSpawn;
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

	private MapObjectSpawn spawn;

	@Override
	public boolean isVisible() {
		return Options.instance.showSpawn.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		if (spawn != null && isInFragmentBounds(fragment)) {
			fragment.addObject(spawn);
		}
	}

	private boolean isInFragmentBounds(Fragment fragment) {
		return fragment.isInBounds(spawn.getWorldX(), spawn.getWorldY());
	}

	@Override
	public void reload() {
		initSpawnObject();
	}

	private void initSpawnObject() {
		Point spawnCenter = getSpawnCenter();
		if (spawnCenter != null) {
			spawn = new MapObjectSpawn(spawnCenter.x, spawnCenter.y);
			spawn.setParentLayer(this);
		} else {
			Log.debug("Unable to find spawn biome.");
			spawn = null;
		}
	}

	private Point getSpawnCenter() {
		Random random = new Random(Options.instance.world.getSeed());
		return MinecraftUtil.findValidLocation(0, 0, 256, VALID_BIOMES, random);
	}
}
