package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.minetest.world.oracle.MinetestBiomeDataOracle;

@ThreadSafe
public class MinetestRiverColorProvider implements ColorProvider {
	private static final int RIVER_COLOR       = 0xC8001090; // 0xAARRGGBB
	private static final int RIVERS_EDGE_COLOR = 0xA05555FF; // 0xAARRGGBB
	private static final int NOT_RIVER_COLOR   = 0x00000000; // 0xAARRGGBB

	private static final int OCEAN_RIVER_MASK = MinetestBiomeDataOracle.BITPLANE_RIVER | MinetestBiomeDataOracle.BITPLANE_OCEAN;
	private static int FRAGSIZE_MINUS1;
				
	public MinetestRiverColorProvider(Resolution resolution) {
		FRAGSIZE_MINUS1 = resolution.getStepsPerFragment() - 1;
	}
			
	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		
		if ((fragment.getBiomeDataAt(x, y) & MinetestBiomeDataOracle.BITPLANE_RIVER) > 0) {
			// draw using a river's edge colour if we're next to a location that isn't river or ocean.
			if ((x >               0 && (fragment.getBiomeDataAt(x - 1, y) & OCEAN_RIVER_MASK) == 0) ||
			    (x < FRAGSIZE_MINUS1 && (fragment.getBiomeDataAt(x + 1, y) & OCEAN_RIVER_MASK) == 0) ||
			    (y >               0 && (fragment.getBiomeDataAt(x, y - 1) & OCEAN_RIVER_MASK) == 0) ||
			    (y < FRAGSIZE_MINUS1 && (fragment.getBiomeDataAt(x, y + 1) & OCEAN_RIVER_MASK) == 0)) {				
				return RIVERS_EDGE_COLOR;
			} else {			
				return RIVER_COLOR;
			}
		} else {
			return NOT_RIVER_COLOR;
		}
	}
}
