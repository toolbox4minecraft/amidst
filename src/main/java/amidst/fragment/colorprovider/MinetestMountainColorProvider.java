package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.minetest.world.oracle.MinetestBiomeDataOracle;

@ThreadSafe
public class MinetestMountainColorProvider implements ColorProvider {
	private static final int MOUNTAIN_COLOR      = 0xF0D0D0D0; // 0xAARRGGBB
	private static final int MOUNTAIN_EDGE_COLOR = 0x80D0D0D0; // 0xAARRGGBB
	private static final int NOT_MOUNTAIN_COLOR  = 0x00000000; // 0xAARRGGBB

	private static final int OCEAN_RIVER_MASK = MinetestBiomeDataOracle.BITPLANE_RIVER | MinetestBiomeDataOracle.BITPLANE_OCEAN;
	private static int FRAGSIZE_MINUS1;
				
	public MinetestMountainColorProvider(Resolution resolution) {
		FRAGSIZE_MINUS1 = resolution.getStepsPerFragment() - 1;
	}
	
	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		
		if ((fragment.getBiomeDataAt(x, y) & MinetestBiomeDataOracle.BITPLANE_MOUNTAIN) > 0) {
			/* Commented out because I haven't found a way to use 1px outlines on mountains that I 
			 * feel is worth the mountain coloring being 4x slower
			// draw using an edge colour if we're next to a location that isn't mountain.
			if ((x >               0 && (fragment.getBiomeDataAt(x - 1, y) & MinetestBiomeDataOracle.BITPLANE_MOUNTAIN) == 0) ||
			    (x < FRAGSIZE_MINUS1 && (fragment.getBiomeDataAt(x + 1, y) & MinetestBiomeDataOracle.BITPLANE_MOUNTAIN) == 0) ||
			    (y >               0 && (fragment.getBiomeDataAt(x, y - 1) & MinetestBiomeDataOracle.BITPLANE_MOUNTAIN) == 0) ||
			    (y < FRAGSIZE_MINUS1 && (fragment.getBiomeDataAt(x, y + 1) & MinetestBiomeDataOracle.BITPLANE_MOUNTAIN) == 0)) {				
				return MOUNTAIN_EDGE_COLOR;
			} else {			
				return MOUNTAIN_COLOR;
			}*/
			return MOUNTAIN_COLOR;
		} else {
			return NOT_MOUNTAIN_COLOR;
		}
	}
}
