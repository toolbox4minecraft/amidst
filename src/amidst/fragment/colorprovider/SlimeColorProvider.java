package amidst.fragment.colorprovider;

import amidst.map.Fragment;
import amidst.minecraft.world.World;

public class SlimeColorProvider implements ColorProvider {
	private static final int SLIME_CHUNK_COLOR = 0xA0FF00FF;
	private static final int NOT_SLIME_CHUNK_COLOR = 0x00000000;

	private final World world;

	public SlimeColorProvider(World world) {
		this.world = world;
	}

	@Override
	public int getColorAt(Fragment fragment, long cornerX, long cornerY, int x,
			int y) {
		if (world.getSlimeChunkOracle().isSlimeChunk(cornerX + x, cornerY + y)) {
			return SLIME_CHUNK_COLOR;
		} else {
			return NOT_SLIME_CHUNK_COLOR;
		}
	}
}
