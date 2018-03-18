package amidst.minetest.world.oracle;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MapgenV7Params;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class BiomeDataOracleV7 extends MinetestBiomeDataOracle {
	private final MapgenV7Params v7params;
	
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
	//private Noise noise_filler_depth; // commented out because it shouldn't been needed for the surface


	/**
	 * Updated by onBiomeProfileUpdate event, can be null.
	 */
	private volatile BiomeProfile biomeProfile;
	
	short mount_zero_level     = 0;
	float float_mount_density  = 0.6f;
	float float_mount_height   = 128.0f;
	float float_mount_exponent = 0.75f;
	short floatland_level      = 1280;
		
	/**
	 * Mountains have to be taller than this to be drawn
	 */
	public static final int cMimimumMountainHeight = 10; 
	
	/**
	 * @param params
	 * @param biomeProfileSelection - if null then a default biomeprofile will be used
	 * @param seed
	 * @throws InvalidNoiseParamsException
	 */
	public BiomeDataOracleV7(MapgenParams mapgenV7Params, BiomeProfileSelection biomeProfileSelection, long seed) {

		super(mapgenV7Params, biomeProfileSelection, seed);
		
		if (params instanceof MapgenV7Params) {
			v7params = (MapgenV7Params)params;
		} else {
			AmidstLogger.error("Error: BiomeDataOracleV7 cannot cast params to v7params. Using defaults instead.");
			v7params = new MapgenV7Params();
		}
		
				
		// This is to avoid a divide-by-zero.
		// Parameter will be saved to map_meta.txt in limited form.
		v7params.float_mount_height = Math.max(v7params.float_mount_height, 1.0f);
		float_mount_height = v7params.float_mount_height;
		
		// 2D noise
		// TODO: Set these to the fragment size, rather than minetest's chunklength, and use the faster noise funcs (set scale appropriately)
		try {
			noise_terrain_base    = new Noise(v7params.np_terrain_base,    this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_terrain_alt     = new Noise(v7params.np_terrain_alt,     this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_terrain_persist = new Noise(v7params.np_terrain_persist, this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_height_select   = new Noise(v7params.np_height_select,   this.seed, params.chunk_length_x, params.chunk_length_z);
			//noise_filler_depth    = new Noise(v7params.np_filler_depth,    this.seed, v7params.chunk_length_x, v7params.chunk_length_z);
	
			if ((v7params.spflags & MapgenV7Params.FLAG_V7_MOUNTAINS) > 0)
				noise_mount_height = new Noise(v7params.np_mount_height, this.seed, params.chunk_length_x, params.chunk_length_z);
	
			if ((v7params.spflags & MapgenV7Params.FLAG_V7_FLOATLANDS) > 0) {
				noise_floatland_base    = new Noise(v7params.np_floatland_base,    this.seed, params.chunk_length_x, params.chunk_length_z);
				noise_float_base_height = new Noise(v7params.np_float_base_height, this.seed, params.chunk_length_x, params.chunk_length_z);
			}		
			
			if ((v7params.spflags & MapgenV7Params.FLAG_V7_RIDGES) > 0) {
				noise_ridge_uwater = new Noise(v7params.np_ridge_uwater, this.seed, params.chunk_length_x, params.chunk_length_z);
			
				// 3D noise, 1-up 1-down overgeneration
				noise_ridge = new Noise(v7params.np_ridge, this.seed, params.chunk_length_x, params.chunk_length_y + 2, params.chunk_length_z);
			}
			
			// 3D noise, 1 up, 1 down overgeneration
			if ((v7params.spflags & (MapgenV7Params.FLAG_V7_MOUNTAINS | MapgenV7Params.FLAG_V7_FLOATLANDS)) > 0) {
				noise_mountain = new Noise(v7params.np_mountain, this.seed, params.chunk_length_x, params.chunk_length_y + 2, params.chunk_length_z);
			}
		} catch (InvalidNoiseParamsException ex) {
			AmidstLogger.error("Invalid v7params from Minetest game. " + ex);
			ex.printStackTrace();
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
		int width = result.length;
		if (width > 0) {
			Resolution resolution = Resolution.from(useQuarterResolution);
			int height = result[0].length;
			int left   = (int) corner.getX();
			int top    = (int) corner.getY();
			int shift = resolution.getShift();
			int step  = resolution.getStep();
			float river_width = 0.14f; // 0.2f is the value used to block spawn, but 0.14 looks more accurate
			int world_z;
			int world_x;
			short biomeValue;
			MinetestBiome[] biomes = getBiomeArray();
			
			/* part of (faster) version of oceans not working yet
			//// Calculate noise for terrain generation
			noise_terrain_persist.perlinMap2D((float)left, (float)top, null);
			float[] persistmap = noise_terrain_persist._result;

			noise_terrain_base.perlinMap2D(   (float)left, (float)top, persistmap);
			noise_terrain_alt.perlinMap2D(    (float)left, (float)top, persistmap);
			noise_height_select.perlinMap2D(  (float)left, (float)top, null);
			*/

			try {			
				int index2d = 0;
				for (int y = 0; y < height; y++) {
					
					world_z = top + (y << shift);
					world_x = left;
					
					// Use -world_z because Minetest uses left-handed coordinates, while Minecraft 
					// and Amidst use right-handed coordinates.
					world_z = -world_z;
					
					for (int x = 0; x < width; x++, index2d++) {
						
						biomeValue = 0;

						/* this (faster) version of oceans not working yet
						if ((x < params.chunk_length_x) && (y < params.chunk_length_z)) {
							index2d = y * params.chunk_length_x + x;
							short surface_y = (short)baseTerrainLevelFromMap(index2d);
							if (surface_y < params.water_level) biomeValue |= BITPLANE_OCEAN;
						}//*/
						
						// Add the ocean bitplane
						int surface_y = (int)baseTerrainLevelAtPoint(world_x, world_z);
						if (surface_y < v7params.water_level) biomeValue |= BITPLANE_OCEAN;
						
						// Add the mountains bitplane
						int surfaceOrSeaLevel = Math.max(surface_y, v7params.water_level);
						float mnt_h_n = Math.max(Noise.NoisePerlin2D(noise_mount_height.np, world_x, world_z, seed), 1.0f);
						float density_gradient = -((float)(surfaceOrSeaLevel - mount_zero_level) / mnt_h_n);
						float mnt_n1 = Noise.NoisePerlin3D(noise_mountain.np, world_x, surfaceOrSeaLevel, world_z, seed);
												
						if (mnt_n1 + density_gradient >= 0.0) {							
							// Mountains are here, but since we only have a 1-bit plane to represent them, lets
							// only draw ones that are quite high
							float mnt_n2 = Noise.NoisePerlin3D(noise_mountain.np, world_x, surfaceOrSeaLevel + cMimimumMountainHeight, world_z, seed);
							density_gradient = -((float)(surfaceOrSeaLevel + cMimimumMountainHeight - mount_zero_level) / mnt_h_n);
							if (mnt_n2 + density_gradient >= 0.0) biomeValue |= BITPLANE_MOUNTAIN;
						}						
						
						// add the river bitplane
						float uwatern = Noise.NoisePerlin2D(noise_ridge_uwater.np, world_x, world_z, seed) * 2;						
						if (Math.abs(uwatern) <= river_width) biomeValue |= BITPLANE_RIVER;
						
						// add the biome index
						// (mask the bitplanes in case the biome returned is -1 (NONE)
						biomeValue |= calcBiomeAtPoint(biomes, world_x, surface_y, world_z).getIndex() & MASK_BITPLANES;
						
						result[x][y] = biomeValue;					
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
