package amidst.map.layer;

import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.object.MapObjectNether;

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
		int chunkX = x + fragment.getChunkX();
		int chunkY = y + fragment.getChunkY();
		if (hasNetherFortress(chunkX, chunkY)) {
			fragment.addObject(new MapObjectNether(x << 4, y << 4)
					.setParent(this));
		}
	}

	private boolean hasNetherFortress(int chunkX, int chunkY) {
		int i = chunkX >> 4;
		int j = chunkY >> 4;

		random.setSeed(i ^ j << 4 ^ Options.instance.seed);
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
