package amidst.map.layer;

import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.minecraft.world.CoordinatesInWorld;

public class SlimeLayer extends ImageLayer {
	private static final int SLIME_CHUNK_COLOR = 0xA0FF00FF;
	private static final int NOT_SLIME_CHUNK_COLOR = 0x00000000;

	private Random random = new Random();

	public SlimeLayer(int layerId) {
		super(Fragment.SIZE >> 4, layerId);
	}

	@Override
	public boolean isVisible() {
		return Options.instance.showSlimeChunks.get();
	}

	@Override
	protected int getColorAt(Fragment fragment, int blockX, int blockY) {
		CoordinatesInWorld corner = fragment.getCorner();
		int x = (int) corner.getXAsChunkResolution() + blockX;
		int y = (int) corner.getYAsChunkResolution() + blockY;
		if (isSlimeChunk(x, y)) {
			return SLIME_CHUNK_COLOR;
		} else {
			return NOT_SLIME_CHUNK_COLOR;
		}
	}

	private boolean isSlimeChunk(int x, int y) {
		updateSeed(x, y);
		return isSlimeChunk();
	}

	private void updateSeed(int x, int y) {
		random.setSeed(getSeed(x, y));
	}

	private long getSeed(int x, int y) {
		return getWorld().getSeed() + x * x * 0x4c1906 + x * 0x5ac0db + y * y
				* 0x4307a7L + y * 0x5f24f ^ 0x3ad8025f;
	}

	private boolean isSlimeChunk() {
		return random.nextInt(10) == 0;
	}
}
