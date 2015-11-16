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

	private MapObjectSpawn spawnObject;

	@Override
	public boolean isVisible() {
		return Options.instance.showSpawn.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		if (spawnObject != null && isInFragmentBounds(fragment)) {
			fragment.addObject(spawnObject);
		}
	}

	private boolean isInFragmentBounds(Fragment fragment) {
		return spawnObject.getGlobalX() >= fragment.getBlockX()
				&& spawnObject.getGlobalX() < fragment.getBlockX()
						+ Fragment.SIZE
				&& spawnObject.getGlobalY() >= fragment.getBlockY()
				&& spawnObject.getGlobalY() < fragment.getBlockY()
						+ Fragment.SIZE;
	}

	@Override
	public void reload() {
		initSpawnObject();
	}

	private void initSpawnObject() {
		Point spawnCenter = getSpawnCenter();
		if (spawnCenter != null) {
			spawnObject = new MapObjectSpawn(spawnCenter.x, spawnCenter.y);
			spawnObject.setParentLayer(this);
		} else {
			Log.debug("Unable to find spawn biome.");
			spawnObject = null;
		}
	}

	private Point getSpawnCenter() {
		Random random = new Random(Options.instance.world.getSeed());
		return MinecraftUtil.findValidLocation(0, 0, 256, VALID_BIOMES, random);
	}
}
