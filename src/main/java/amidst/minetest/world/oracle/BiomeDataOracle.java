package amidst.minetest.world.oracle;

import java.util.Random;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.fragment.IBiomeDataOracle;
import amidst.gameengineabstraction.CoordinateSystem;
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
	
	private Noise noise_terrain_base;
	private Noise noise_terrain_alt;
	private Noise noise_terrain_persist;
	private Noise noise_height_select;
	private Noise noise_mount_height;
	private Noise noise_ridge_uwater;
	private Noise noise_floatland_base;
	private Noise noise_float_base_height;
	private Noise noise_mountain;
	private Noise noise_ridge;
	private Noise noise_filler_depth;
	
	short mount_zero_level     = 0;
	float float_mount_density  = 0.6f;
	float float_mount_height   = 128.0f;
	float float_mount_exponent = 0.75f;
	short floatland_level      = 1280;
	
	
	public static final int BITPLANE_RIVER       = 0x4000;
	public static final int BITPLANE_OCEAN       = 0x2000;
	public static final int BITPLANE_MOUNTAIN    = 0x1000;
	public static final int MASK_BITPLANES       = ~(BITPLANE_RIVER | BITPLANE_OCEAN | BITPLANE_MOUNTAIN);
	

	public BiomeDataOracle(MapgenV7Params params, long seed) throws InvalidNoiseParamsException {
		//this.seed = (int)(seed & 0xFFFFFFFFL);
		this.seed = (int)seed;		
		this.params = params;
		
		// This is to avoid a divide-by-zero.
		// Parameter will be saved to map_meta.txt in limited form.
		params.float_mount_height = Math.max(params.float_mount_height, 1.0f);
		float_mount_height = params.float_mount_height;
		
		// 2D noise
		noise_terrain_base    = new Noise(params.np_terrain_base,    this.seed, params.chunk_length_x, params.chunk_length_z);
		noise_terrain_alt     = new Noise(params.np_terrain_alt,     this.seed, params.chunk_length_x, params.chunk_length_z);
		noise_terrain_persist = new Noise(params.np_terrain_persist, this.seed, params.chunk_length_x, params.chunk_length_z);
		noise_height_select   = new Noise(params.np_height_select,   this.seed, params.chunk_length_x, params.chunk_length_z);
		noise_filler_depth    = new Noise(params.np_filler_depth,    this.seed, params.chunk_length_x, params.chunk_length_z);

		if ((params.spflags & MapgenV7Params.FLAG_V7_MOUNTAINS) > 0)
			noise_mount_height = new Noise(params.np_mount_height, this.seed, params.chunk_length_x, params.chunk_length_z);

		if ((params.spflags & MapgenV7Params.FLAG_V7_FLOATLANDS) > 0) {
			noise_floatland_base    = new Noise(params.np_floatland_base,    this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_float_base_height = new Noise(params.np_float_base_height, this.seed, params.chunk_length_x, params.chunk_length_z);
		}		
		
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
	
	float baseTerrainLevelFromMap(int index)
	{
		// #define rangelim(d, min, max) ((d) < (min) ? (min) : ((d) > (max) ? (max) : (d)))
		// float hselect     = rangelim(noise_height_select->result[index], 0.0, 1.0);
		float hselect = noise_height_select._result[index];		
		hselect = (hselect < 0.0f) ? 0.0f : ((hselect > 1.0f) ? 1.0f : hselect);
		
		float height_base = noise_terrain_base._result[index];
		float height_alt  = noise_terrain_alt._result[index];

		if (height_alt > height_base)
			return height_alt;

		return (height_base * hselect) + (height_alt * (1.0f - hselect));
	}
	
	float baseTerrainLevelAtPoint(int x, int z)
	{
		float hselect = Noise.NoisePerlin2D(noise_height_select.np, x, z, seed);
		hselect = (hselect < 0.0f) ? 0.0f : ((hselect > 1.0f) ? 1.0f : hselect);

		float persist = Noise.NoisePerlin2D(noise_terrain_persist.np, x, z, seed);

		noise_terrain_base.np.persist = persist;
		float height_base = Noise.NoisePerlin2D(noise_terrain_base.np, x, z, seed);

		noise_terrain_alt.np.persist = persist;
		float height_alt = Noise.NoisePerlin2D(noise_terrain_alt.np, x, z, seed);

		if (height_alt > height_base)
			return height_alt;

		return (height_base * hselect) + (height_alt * (1.0f - hselect));
	}
	
	

	@Override
	public short populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		Resolution resolution = Resolution.from(useQuarterResolution);
		int width = result.length;
		if (width > 0) {
			int height = result[0].length;
			int left   = (int) corner.getX();
			int top    = (int) corner.getY();
			int shift = resolution.getShift();
			int step  = resolution.getStep();
			float river_width = 0.19f; // 0.2f is the value used to block spawn, but 1.9 looks more accurate
			int world_z;
			int world_x;
			short biomeValue;
			
			//// Calculate noise for terrain generation
			noise_terrain_persist.perlinMap2D((float)left, (float)top, null);
			float[] persistmap = noise_terrain_persist._result;

			noise_terrain_base.perlinMap2D(   (float)left, (float)top, persistmap);
			noise_terrain_alt.perlinMap2D(    (float)left, (float)top, persistmap);
			noise_height_select.perlinMap2D(  (float)left, (float)top, null);
			

			try {			
				int index2d = 0;
				for (int y = 0; y < height; y++) {
					
					world_z = top + (y << shift);
					world_x = left;
					
					for (int x = 0; x < width; x++, index2d++) {
						
						biomeValue = 0;

						/* this (faster) version of oceans not working yet
						if ((x < params.chunk_length_x) && (y < params.chunk_length_z)) {
							index2d = y * params.chunk_length_x + x;
							short surface_y = (short)baseTerrainLevelFromMap(index2d);
							if (surface_y < params.water_level) biomeValue |= BITPLANE_OCEAN;
						}*/
						
						short surface_y = (short)baseTerrainLevelAtPoint(world_x, -world_z);
						if (surface_y < params.water_level) biomeValue |= BITPLANE_OCEAN;
						
						// add the river bitplane
						// use -world_z because Minetest uses left-handed coordinates, while
						// Minecraft and Amidst use right-handed coordinates.
						float uwatern = Noise.NoisePerlin2D(noise_ridge_uwater.np, world_x, -world_z, seed) * 2;						
						if (Math.abs(uwatern) <= river_width) biomeValue |= BITPLANE_RIVER;

						
						// lets set a default biome until we put the code in
						biomeValue |= 1; // 1 is plains
						
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
	
	/**
	 * Gets the native coordinate system of the game-engine this biome
	 * data represents. This is only needed if you want to know how the
	 * game would describe a biome location. 
	 */
	@Override
	public CoordinateSystem getNativeCoordinateSystem() {
		// Minetest uses left-handed coords
		return CoordinateSystem.LEFT_HANDED;
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
