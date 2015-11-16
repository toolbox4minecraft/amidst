package amidst.map.layer;

import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;

public class SlimeLayer extends ImageLayer {
	private Random random = new Random();

	public SlimeLayer() {
		super(Fragment.SIZE >> 4);
	}

	@Override
	public boolean isVisible() {
		return Options.instance.showSlimeChunks.get();
	}

	@Override
	public void drawToCache(Fragment fragment) {
		int[] dataCache = Fragment.getImageRGBDataCache();
		for (int y = 0; y < getSize(); y++) {
			for (int x = 0; x < getSize(); x++) {
				dataCache[getCacheIndex(x, y)] = getColorAt(fragment, x, y);
			}
		}
		fragment.setImageRGB(getLayerId(), dataCache);
	}

	private int getCacheIndex(int x, int y) {
		return y * getSize() + x;
	}

	private int getColorAt(Fragment fragment, int x, int y) {
		updateSeed(fragment, x, y);
		return getNextValue();
	}

	private void updateSeed(Fragment fragment, int x, int y) {
		int xPosition = fragment.getChunkXInWorld() + x;
		int yPosition = fragment.getChunkYInWorld() + y;
		random.setSeed(getSeed(xPosition, yPosition));
	}

	private long getSeed(int xPosition, int yPosition) {
		return Options.instance.world.getSeed() + xPosition * xPosition
				* 0x4c1906 + xPosition * 0x5ac0db + yPosition * yPosition
				* 0x4307a7L + yPosition * 0x5f24f ^ 0x3ad8025f;
	}

	private int getNextValue() {
		if (random.nextInt(10) == 0) {
			return 0xA0FF00FF;
		} else {
			return 0x00000000;
		}
	}
}
