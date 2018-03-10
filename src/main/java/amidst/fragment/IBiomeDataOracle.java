package amidst.fragment;

import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

public interface IBiomeDataOracle {

	// Returns the biome-index mask: a mask you should apply to the 
	// returned biome data if you wish to use it like a biome index.
	// This will remove any bits from the integer that are being used
	// to indicate other things, such as 1-bit overlay biome layers 
	// for minetest rivers and oceans	
	public short populateArray(
			CoordinatesInWorld corner, 
			short[][] result,
			boolean useQuarterResolution
	);		
}
