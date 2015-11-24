package amidst.map.layer;

import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.minecraft.world.Resolution;

public class SlimeLayer extends ImageLayer {
	private static final int SLIME_CHUNK_COLOR = 0xA0FF00FF;
	private static final int NOT_SLIME_CHUNK_COLOR = 0x00000000;

	private Random random = new Random();

	public SlimeLayer() {
		super(LayerType.SLIME, Resolution.CHUNK);
	}

	@Override
	public boolean isVisible() {
		return Options.instance.showSlimeChunks.get();
	}

	@Override
	protected int getColorAt(Fragment fragment, long cornerX, long cornerY,
			int x, int y) {
		if (isSlimeChunk(cornerX + x, cornerY + y)) {
			return SLIME_CHUNK_COLOR;
		} else {
			return NOT_SLIME_CHUNK_COLOR;
		}
	}

	private boolean isSlimeChunk(long xAsResolution, long yAsResolution) {
		updateSeed(xAsResolution, yAsResolution);
		return isSlimeChunk();
	}

	private void updateSeed(long xAsResolution, long yAsResolution) {
		random.setSeed(getSeed(xAsResolution, yAsResolution));
	}

	private long getSeed(long xAsResolution, long yAsResolution) {
		return getWorld().getSeed() + xAsResolution * xAsResolution * 0x4c1906
				+ xAsResolution * 0x5ac0db + yAsResolution * yAsResolution
				* 0x4307a7L + yAsResolution * 0x5f24f ^ 0x3ad8025f;
	}

	private boolean isSlimeChunk() {
		return random.nextInt(10) == 0;
	}
}
