package amidst.minetest.world.oracle;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.Constants;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MapgenV7Params;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class BiomeDataOracleV7 extends MinetestBiomeDataOracle {
	
	private boolean isFloatlands;
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
	//private Noise noise_ridge;
	//private Noise noise_filler_depth; // commented out because it shouldn't been needed for the surface


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
	public BiomeDataOracleV7(boolean is_floatlands, MapgenParams mapgenV7Params, BiomeProfileSelection biomeProfileSelection, long seed) {

		super(mapgenV7Params, biomeProfileSelection, seed);
		this.isFloatlands = is_floatlands;
		
		if (params instanceof MapgenV7Params) {
			v7params = (MapgenV7Params)params;
		} else {
			AmidstLogger.error("Error: BiomeDataOracleV7 cannot cast params to v7params. Using defaults instead.");
			this.params = v7params = new MapgenV7Params();
		}

		// This is to avoid a divide-by-zero.
		// Parameter will be saved to map_meta.txt in limited form.
		v7params.float_mount_height = Math.max(v7params.float_mount_height, 1.0f);
		float_mount_height = v7params.float_mount_height;

		// 2D noise
		// TODO: Set these to the fragment size, rather than minetest's chunklength, and use the faster noise funcs (set scale appropriately)
		try {
			if (isFloatlands) {
				noise_floatland_base    = new Noise(v7params.np_floatland_base,    this.seed, params.chunk_length_x, params.chunk_length_z);
				noise_float_base_height = new Noise(v7params.np_float_base_height, this.seed, params.chunk_length_x, params.chunk_length_z);				
			} else {
				noise_terrain_base      = new Noise(v7params.np_terrain_base,      this.seed, params.chunk_length_x, params.chunk_length_z);
				noise_terrain_alt       = new Noise(v7params.np_terrain_alt,       this.seed, params.chunk_length_x, params.chunk_length_z);
				noise_terrain_persist   = new Noise(v7params.np_terrain_persist,   this.seed, params.chunk_length_x, params.chunk_length_z);
				noise_height_select     = new Noise(v7params.np_height_select,     this.seed, params.chunk_length_x, params.chunk_length_z);
				//noise_filler_depth      = new Noise(v7params.np_filler_depth,    this.seed, v7params.chunk_length_x, v7params.chunk_length_z);
			}
				
			// Mountains layer might be displayed even if mountains are off, so initialize these regardless
			noise_mount_height    = new Noise(v7params.np_mount_height,    this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_mountain        = new Noise(v7params.np_mountain,        this.seed, params.chunk_length_x, params.chunk_length_y + 2, params.chunk_length_z);

			// River layer might be displayed even if ridges are off, so initialize these regardless
			noise_ridge_uwater = new Noise(v7params.np_ridge_uwater, this.seed, params.chunk_length_x, params.chunk_length_z);			
			// 3D noise, 1-up 1-down overgeneration
			//noise_ridge = new Noise(v7params.np_ridge, this.seed, params.chunk_length_x, params.chunk_length_y + 2, params.chunk_length_z);
			
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

	/**
	 * A logarithmic-ish search for some ground with air directly above.
	 * (If it's the top of a floating island or ground beneath an underhang then tough, but because we start
	 * low, it should find what the player will consider the ground most of the time)
	 * @param lastHeight - the height of the previously calculated adjacent coord. set to Short.MIN_VALUE if not known
	 * @param mnt_h_n - the 2D perlin noise of noise_mount_height for the location
	 * @param highestGround - highest known ground height
	 * @param lowestAir - lowest known air height.
	 * @return height of the first highest level of ground located with air directly above.
	 */
	int findMountainHeight(int lastHeight, float mnt_h_n, int highestGround, int x, int z) {
		
		int lowestAir     = Short.MAX_VALUE;    // We don't know where the air starts
		int testHeight    = highestGround + 4;  // Seems like a good start point if we don't know anything else (most of the time this value is overwritten with lastAirHeight, so it's not too important).

		if (lastHeight != Short.MIN_VALUE) {
			// Perhaps the height hasn't changed and we can save some tests with this information: We can check if
			// height is the same with only two tests (and only 1 will be needed if lastHeight was highestGround).
			int lastAirHeight = lastHeight + 1;
			
			float density_gradient = -((float)(lastAirHeight - mount_zero_level) / mnt_h_n);
			float mnt_n = Noise.NoisePerlin3D(noise_mountain.np, x, lastAirHeight, z, seed);			
			boolean isAir = mnt_n + density_gradient < 0.0f;
			
			if (isAir) {
				lowestAir = lastAirHeight;
				testHeight = lastHeight;
			} else {
				// Nope, height has changed, we will have to do a proper search
				highestGround = lastAirHeight;
				testHeight = lastAirHeight + 1; // perhaps the land has only raised by 1, we can live in hope
			}
		}

		while (highestGround + 1 < lowestAir) {
			float density_gradient = -((float)(testHeight - mount_zero_level) / mnt_h_n);
			float mnt_n = Noise.NoisePerlin3D(noise_mountain.np, x, testHeight, z, seed);			
			boolean isAir = mnt_n + density_gradient < 0.0f;
			
			if (isAir) {
				lowestAir = testHeight;
				testHeight -= ((testHeight - highestGround) / 2);
			} else {
				int previousHighestGround = highestGround;
				highestGround = testHeight;
				if (lowestAir == Short.MAX_VALUE) {
					testHeight += 2 * (testHeight - previousHighestGround);
				} else {
					testHeight += (lowestAir - testHeight) / 2;
				}
			}
		}
		if (highestGround + 1 != lowestAir) {
			AmidstLogger.error("Ground search alg failed! highestGround: " + highestGround + ", lowestAir: " + lowestAir);
		}
		
		return highestGround;		
	}
	
	@Override
	public short populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		return isFloatlands ? 
				populateArray_floatlands(corner, result, useQuarterResolution) :
				populateArray_groundlevel(corner, result, useQuarterResolution);
	}
	
	public short populateArray_groundlevel(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		
		// The v7 mapgen terrain has been officially stable since 0.4.16 when it was made the default 
		// mapgen, however the optional floatlands (disabled by default) are not stable yet, maybe they 
		// will be for 0.5.0.
		// See https://forum.minetest.net/viewtopic.php?f=18&t=19132						
		
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
					
					int lastMountainHeight = Short.MIN_VALUE; // minvalue will be the "not-known" value
					
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
						if (surface_y < params.water_level) biomeValue |= BITPLANE_OCEAN;
												
						// Add the mountains bitplane
						int surfaceOrSeaLevel = Math.max(surface_y, params.water_level);
						float mnt_h_n = Math.max(Noise.NoisePerlin2D(noise_mount_height.np, world_x, world_z, seed), 1.0f);
						float density_gradient = -((float)(surfaceOrSeaLevel - mount_zero_level) / mnt_h_n);
						float mnt_n1 = Noise.NoisePerlin3D(noise_mountain.np, world_x, surfaceOrSeaLevel, world_z, seed);
												
						if (mnt_n1 + density_gradient >= 0.0) {							
							// Mountains are here
							int mountainHeight = findMountainHeight(lastMountainHeight, mnt_h_n, surfaceOrSeaLevel, world_x, world_z);
							lastMountainHeight = mountainHeight;
																			
							// since we only have a 1-bit plane to represent them, lets
							// only draw ones that are quite high
							if (mountainHeight >= surfaceOrSeaLevel + cMimimumMountainHeight) {
								biomeValue |= BITPLANE_MOUNTAIN;
							}
							
							if ((v7params.spflags & MapgenV7Params.FLAG_V7_MOUNTAINS) > 0) {
								surface_y = mountainHeight;
								// Remove ocean if mountains rise above sea level.
								if ((biomeValue & BITPLANE_OCEAN) > 0 && mountainHeight > surfaceOrSeaLevel) {
									biomeValue -= BITPLANE_OCEAN;
								}
							}
						} else {
							lastMountainHeight = Short.MIN_VALUE; // minvalue will be the "not-known" value
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

	public boolean isFloatlandMountain(int x, int height_above_floatland_level, int z) {
		float density_gradient = (float) -Math.pow(height_above_floatland_level / float_mount_height, float_mount_exponent);
		float floatn = Noise.NoisePerlin3D(noise_mountain.np, x, height_above_floatland_level + floatland_level, z, seed) + float_mount_density;
		return floatn + density_gradient >= 0.0f;
	}
	
	public short populateArray_floatlands(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		
		// The v7 mapgen terrain has been officially stable since 0.4.16 when it was made the default 
		// mapgen, however the optional floatlands (disabled by default) are not stable yet, maybe they 
		// will be for 0.5.0.
		// See https://forum.minetest.net/viewtopic.php?f=18&t=19132						
		
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
				for (int z = 0; z < height; z++) {
					
					world_z = top + (z << shift);
					world_x = left;
					
					// Use -world_z because Minetest uses left-handed coordinates, while Minecraft 
					// and Amidst use right-handed coordinates.
					world_z = -world_z;
					
					for (int x = 0; x < width; x++) {
						
						biomeValue = 0;

						short surface_y = Constants.MAX_MAP_GENERATION_LIMIT;
	
						// Calculate floatland plains
						float n_base = Noise.NoisePerlin2D(noise_floatland_base.np, world_x, world_z, seed);
						if (n_base > 0.0f) {
							float n_base_height = Math.max(1.0f, Noise.NoisePerlin2D(noise_float_base_height.np, world_x, world_z, seed));
							float amp = n_base * n_base_height;
							float ridge = n_base_height / 3.0f;
	
							if (amp > ridge * 2.0f) {
								// Lake bed
								surface_y = (short)(floatland_level - (amp - ridge * 2.0f) / 2.0f);
								biomeValue |= BITPLANE_OCEAN;
							} else {
								// Hills and ridges
								float diff = Math.abs(amp - ridge) / ridge;
								// Smooth ridges using the 'smoothstep function'
								float smooth_diff = diff * diff * (3.0f - 2.0f * diff);
								surface_y = (short)(floatland_level + ridge - smooth_diff * ridge);
							}
						}
							
						// Calculate floatland mountains	
						if (isFloatlandMountain(world_x, 0, world_z)) {
							// Mountains are here
							if (surface_y == Constants.MAX_MAP_GENERATION_LIMIT) {
								// there aren't any floatlands plains here to have already set surface_y, so set 
								// it to what we know of these mountains.
								surface_y = floatland_level; // we know there is ground here								
							}

							// Remove ocean since mountains are at least at floatland_level.
							// This makes the oceans more correct, but the biome here might be wrong since
							// we haven't worked out the real height.
							if ((biomeValue & BITPLANE_OCEAN) > 0) biomeValue -= BITPLANE_OCEAN;							
							
							// check if mountains are lower than 4, so that default biome beaches are right
							// TODO: Perhaps do a logarithmic search for the height like we do in v5 oracle
							if (isFloatlandMountain(world_x, 4, world_z)) {
								// Ground is at least 4 high.
								// Mountains are here, but since we only have a 1-bit plane to represent them, lets
								// only draw ones that are quite high
								if (isFloatlandMountain(world_x, cMimimumMountainHeight, world_z)) {
									surface_y = (short) Math.max(surface_y, cMimimumMountainHeight + floatland_level); // we know the ground here is at least 4 high									
									biomeValue |= BITPLANE_MOUNTAIN;
								} else {
									surface_y = (short) Math.max(surface_y, 4 + floatland_level); // we know the ground here is at least 4 high									
								}
							}
						}
										
						if (surface_y < Constants.MAX_MAP_GENERATION_LIMIT) {
							// add the biome index
							// (mask the bitplanes in case the biome returned is -1 (NONE)
							biomeValue |= calcBiomeAtPoint(biomes, world_x, surface_y, world_z).getIndex() & MASK_BITPLANES;
						} else {
							// It's a long fall
							biomeValue = (short) MinetestBiome.VOID.getIndex();							
						}
						
						result[x][z] = biomeValue;					
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
