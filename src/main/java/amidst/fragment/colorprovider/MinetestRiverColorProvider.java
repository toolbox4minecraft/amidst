package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.minetest.world.oracle.BiomeDataOracle;

@ThreadSafe
public class MinetestRiverColorProvider implements ColorProvider {
	private static final int RIVER_COLOR     = 0xA0FE80FA;
	private static final int NOT_RIVER_COLOR = 0x00000000;

	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		
		if ((fragment.getBiomeDataAt(x, y) & BiomeDataOracle.BITPLANE_RIVER) > 0) {
			return RIVER_COLOR;
		} else {
			return NOT_RIVER_COLOR;
		}
	}
}
