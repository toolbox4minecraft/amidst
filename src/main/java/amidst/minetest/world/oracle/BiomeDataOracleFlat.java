package amidst.minetest.world.oracle;

import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.MapgenFlatParams;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfileSelection;

public class BiomeDataOracleFlat extends MinetestBiomeDataOracle {

	private MapgenFlatParams flatParams = null;
	
	private Noise noise_terrain;
	
	
	public BiomeDataOracleFlat(MapgenParams params, BiomeProfileSelection biomeProfileSelection, long seed) {
		super(params, biomeProfileSelection, seed);

		if (params instanceof MapgenFlatParams) {
			flatParams = (MapgenFlatParams)params;
		} else {
			AmidstLogger.error("Error: BiomeDataOracleFlat cannot cast params to flatParams. Using defaults instead.");
			flatParams = new MapgenFlatParams();
		}
	}

	@Override
	public short populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		
		int width = result.length;
		if (width > 0) {
			Resolution resolution = Resolution.from(useQuarterResolution);
			int height = result[0].length;
			int left   = (int) corner.getX();
			int top    = (int) corner.getY();
			int shift = resolution.getShift();
			int step  = resolution.getStep();
			int world_z;
			int world_x;
			short biomeValue;
			MinetestBiome[] biomes = getBiomeArray();
			
			try {			
				int index3d = 0;
				int index2d = 0;
				for (int z = 0; z < height; z++) {
					
					world_z = top + (z << shift);
					world_x = left;
					
					// Use -world_z because Minetest uses left-handed coordinates, while Minecraft 
					// and Amidst use right-handed coordinates.
					world_z = -world_z;
					
					for (int x = 0; x < width; x++, index2d++, index3d++) {
						
						// (noise_height 'offset' is the average level of terrain. At least 50% of
						// terrain will be below this)						
						result[x][z] = (short)calcBiomeAtPoint(biomes, world_x, flatParams.ground_level, world_z).getIndex();						
						
						world_x += step;
					}
				}
			} catch (Exception e) {
				AmidstLogger.error(e);
				AmidstMessageBox.displayError("Error", e);
			}
		}
		return MASK_BITPLANES;
		
	}	
}
