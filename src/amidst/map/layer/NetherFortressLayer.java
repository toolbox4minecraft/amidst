package amidst.map.layer;

import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.object.MapObject;
import amidst.map.object.SimpleMapObject;

public class NetherFortressLayer extends IconLayer {
	private Random random = new Random();

	@Override
	public boolean isVisible() {
		return Options.instance.showNetherFortresses.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				generateAt(fragment, x, y);
			}
		}
	}

	private void generateAt(Fragment fragment, int x, int y) {
		int chunkX = x + fragment.getChunkXInWorld();
		int chunkY = y + fragment.getChunkYInWorld();
		if (hasNetherFortress(chunkX, chunkY)) {
			MapObject mapObject = createMapObject(x << 4, y << 4);
			mapObject.setParentLayer(this);
			fragment.addObject(mapObject);
		}
	}

	private SimpleMapObject createMapObject(int x2, int y2) {
		return new SimpleMapObject(MapMarkers.NETHER_FORTRESS, x2, y2);
	}

	private boolean hasNetherFortress(int chunkX, int chunkY) {
		int i = chunkX >> 4;
		int j = chunkY >> 4;

		random.setSeed(i ^ j << 4 ^ Options.instance.world.getSeed());
		random.nextInt();

		if (random.nextInt(3) != 0) {
			return false;
		}
		if (chunkX != (i << 4) + 4 + random.nextInt(8)) {
			return false;
		}

		return chunkY == (j << 4) + 4 + random.nextInt(8);
	}
}
