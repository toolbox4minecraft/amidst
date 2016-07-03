package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;

@ThreadSafe
public class SlimeColorProvider implements ColorProvider {
	private static final int SLIME_CHUNK_COLOR = 0xA0FE80FA;
	private static final int NOT_SLIME_CHUNK_COLOR = 0x00000000;

	private final SlimeChunkOracle slimeChunkOracle;

	public SlimeColorProvider(SlimeChunkOracle slimeChunkOracle) {
		this.slimeChunkOracle = slimeChunkOracle;
	}

	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		if (slimeChunkOracle.isSlimeChunk(cornerX + x, cornerY + y)) {
			return SLIME_CHUNK_COLOR;
		} else {
			return NOT_SLIME_CHUNK_COLOR;
		}
	}
}
