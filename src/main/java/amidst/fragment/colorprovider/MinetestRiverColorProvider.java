package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.minetest.world.oracle.MinetestBiomeDataOracle;

@ThreadSafe
public class MinetestRiverColorProvider implements ColorProvider {
	private static final int RIVER_COLOR            = 0xC8062170; // 0xAARRGGBB
	private static final int RIVERS_EDGE_COLOR      = 0x70949d96; // 0xAARRGGBB
	private static final int RIVERS_EDGE_COLOR_THIN = 0x78062170; // 0xAARRGGBB
	private static final int NOT_RIVER_COLOR        = 0x00000000; // 0xAARRGGBB

	private static final int OCEAN_RIVER_MASK = MinetestBiomeDataOracle.BITPLANE_RIVER | MinetestBiomeDataOracle.BITPLANE_OCEAN;
	private static int FRAGSIZE_MINUS1;
	
	private int riversEdgeColor;
				
	public MinetestRiverColorProvider(WorldType world_type, Resolution resolution) {
		FRAGSIZE_MINUS1 = resolution.getStepsPerFragment() - 1;
		
		if (world_type == WorldType.VALLEYS) {
			// Valleys mapgen rivers tend to be one or two pixels wide in Amidst,
			// so they don't have "riversides", so rather than using an edge color to 
			// suggest a riverbank, use the color to suggest a creek.
			riversEdgeColor = RIVERS_EDGE_COLOR_THIN;
		} else {
			riversEdgeColor = RIVERS_EDGE_COLOR;			
		}
	}
			
	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		
		if ((fragment.getBiomeDataAt(x, y) & MinetestBiomeDataOracle.BITPLANE_RIVER) > 0) {
			// draw using a river's edge colour if we're next to a location that isn't river or ocean.
			if ((x >               0 && (fragment.getBiomeDataAt(x - 1, y) & OCEAN_RIVER_MASK) == 0) ||
			    (x < FRAGSIZE_MINUS1 && (fragment.getBiomeDataAt(x + 1, y) & OCEAN_RIVER_MASK) == 0) ||
			    (y >               0 && (fragment.getBiomeDataAt(x, y - 1) & OCEAN_RIVER_MASK) == 0) ||
			    (y < FRAGSIZE_MINUS1 && (fragment.getBiomeDataAt(x, y + 1) & OCEAN_RIVER_MASK) == 0)) {				
				return riversEdgeColor;
			} else {			
				return RIVER_COLOR;
			}
		} else {
			return NOT_RIVER_COLOR;
		}
	}
}
