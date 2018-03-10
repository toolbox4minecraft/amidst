package amidst.minetest.world.oracle;

import java.util.Random;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.fragment.IBiomeDataOracle;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenV7Params;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@Immutable
public class BiomeDataOracle implements IBiomeDataOracle {
	private final int seed;
	private final MapgenV7Params params;
	
	private Noise noise_terrain_alt;
	private Noise noise_ridge_uwater;
	private Noise noise_mountain;
	private Noise noise_ridge;
	
	
	public static final int BITPLANE_RIVER       = 0x4000;
	public static final int BITPLANE_OCEAN       = 0x2000;
	public static final int BITPLANE_MOUNTAIN    = 0x1000;
	public static final int MASK_BITPLANES       = ~(BITPLANE_RIVER | BITPLANE_OCEAN | BITPLANE_MOUNTAIN);
	

	public BiomeDataOracle(MapgenV7Params params, long seed) throws InvalidNoiseParamsException {
		//this.seed = (int)(seed & 0xFFFFFFFFL);
		this.seed = (int)seed;		
		this.params = params;
		
		if ((params.spflags & MapgenV7Params.FLAG_V7_RIDGES) > 0) {
			noise_ridge_uwater = new Noise(params.np_ridge_uwater, this.seed, params.chunk_length_x, params.chunk_length_z);
		
			// 3D noise, 1-up 1-down overgeneration
			noise_ridge = new Noise(params.np_ridge, this.seed, params.chunk_length_x, params.chunk_length_y + 2, params.chunk_length_z);
		}
		
		// 3D noise, 1 up, 1 down overgeneration
		if ((params.spflags & (MapgenV7Params.FLAG_V7_MOUNTAINS | MapgenV7Params.FLAG_V7_FLOATLANDS)) > 0) {
			noise_mountain = new Noise(params.np_mountain, this.seed, params.chunk_length_x, params.chunk_length_y + 2, params.chunk_length_z);
		}
	}

	public short populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		Resolution resolution = Resolution.from(useQuarterResolution);
		int width = result.length;
		if (width > 0) {
			int height = result[0].length;
			int left   = (int) corner.getX();
			int top    = (int) corner.getY();
			int shift = resolution.getShift();
			int step  = resolution.getStep();
			float river_width = 0.2f;
			int world_y;
			int world_x;
			short biomeValue;

			try {			
				for (int y = 0; y < height; y++) {
					
					world_y = top + (y << shift);
					world_x = left;
					
					for (int x = 0; x < width; x++) {
						
						biomeValue = 0;
						
						// add the river bitplane
						float uwatern = Noise.NoisePerlin2D(noise_ridge_uwater.np, world_x, world_y, seed) * 2;						
						if (Math.abs(uwatern) <= river_width) biomeValue |= BITPLANE_RIVER;

						
						result[x][y] = biomeValue;					
						world_x += step;
					}
				}
				

				//copyToResult(result, width, height, getBiomeData(left, top, width, height, useQuarterResolution));
			} catch (Exception e) {
				AmidstLogger.error(e);
				AmidstMessageBox.displayError("Error", e);
			}
		}
		return MASK_BITPLANES;
	}
	
	/*
	public static void copyToResult(short[][] result, int width, int height, int[] biomeData) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result[x][y] = (short) biomeData[getBiomeDataIndex(x, y, width)];
			}
		}
	}	
	
	private int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws UnsupportedOperationException {
				
		// return minecraftInterface.getBiomeData(x, y, width, height, useQuarterResolution);
		throw new UnsupportedOperationException("getBiomeData (minetest)");
	}*/
	
}
