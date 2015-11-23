package amidst.map.layer;

import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;

public class SlimeLayer extends ImageLayer {
	private Random random = new Random();

	public SlimeLayer(int layerId) {
		super(Fragment.SIZE >> 4, layerId);
	}

	@Override
	public boolean isVisible() {
		return Options.instance.showSlimeChunks.get();
	}

	@Override
	protected int getColorAt(Fragment fragment, int x, int y) {
		updateSeed(fragment, x, y);
		return getNextValue();
	}

	// TODO: use longs?
	private void updateSeed(Fragment fragment, int x, int y) {
		int xPosition = (int) fragment.getCorner().getXAsChunkResolution() + x;
		int yPosition = (int) fragment.getCorner().getYAsChunkResolution() + y;
		random.setSeed(getSeed(xPosition, yPosition));
	}

	private long getSeed(int xPosition, int yPosition) {
		return getWorld().getSeed() + xPosition * xPosition * 0x4c1906
				+ xPosition * 0x5ac0db + yPosition * yPosition * 0x4307a7L
				+ yPosition * 0x5f24f ^ 0x3ad8025f;
	}

	private int getNextValue() {
		if (random.nextInt(10) == 0) {
			return 0xA0FF00FF;
		} else {
			return 0x00000000;
		}
	}
}
