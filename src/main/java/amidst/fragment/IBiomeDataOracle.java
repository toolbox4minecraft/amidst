package amidst.fragment;

import amidst.gameengineabstraction.CoordinateSystem;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

public interface IBiomeDataOracle {

	/**
	 * Fills result with biome data.
	 *
	 * Regardless of the game-engine's actual coordinate system (left or 
	 * right-handed), populateArray() assumes data will be drawn in a 
	 * right-handed coordinate system, and that corner is specified in a
	 * right-handed coordinate system.
	 * 
	 * @return the biome-index mask: a mask you should apply to the 
	 * returned biome data if you wish to use it like a biome index.
	 * This will remove any bits from the integer that are being used
	 * to indicate other things, such as 1-bit overlay biome layers 
	 * for minetest rivers and oceans.
	 */
	public short populateArray(
			CoordinatesInWorld corner, 
			short[][] result,
			boolean useQuarterResolution
	);		
	
	/**
	 * Gets the native coordinate system of the game-engine this biome
	 * data represents. This is only needed if you want to know how the
	 * game would describe a biome location. 
	 */
	public CoordinateSystem getNativeCoordinateSystem();
}
