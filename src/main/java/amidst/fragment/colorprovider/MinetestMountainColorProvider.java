package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.minetest.world.oracle.BiomeDataOracle;

@ThreadSafe
public class MinetestMountainColorProvider implements ColorProvider {
	private static final int MOUNTAIN_COLOR     = 0xE0D0D0D0; // 0xAARRGGBB
	private static final int NOT_MOUNTAIN_COLOR = 0x00000000; // 0xAARRGGBB

	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		
		if ((fragment.getBiomeDataAt(x, y) & BiomeDataOracle.BITPLANE_MOUNTAIN) > 0) {
			return MOUNTAIN_COLOR;
		} else {
			return NOT_MOUNTAIN_COLOR;
		}
	}
}
