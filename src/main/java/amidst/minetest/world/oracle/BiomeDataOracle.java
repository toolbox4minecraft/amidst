package amidst.minetest.world.oracle;

import java.util.Collection;
import java.util.Map.Entry;

import amidst.documentation.Immutable;
import amidst.fragment.IBiomeDataOracle;
import amidst.gameengineabstraction.CoordinateSystem;
import amidst.gameengineabstraction.world.biome.IBiome;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenV7Params;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.MinetestBiomeProfileImpl;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.settings.biomeprofile.BiomeProfileUpdateListener;

@Immutable
public class BiomeDataOracle implements IBiomeDataOracle, BiomeProfileUpdateListener {
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
	
	
	public static final int BITPLANE_RIVER       = 0x4000;
	public static final int BITPLANE_OCEAN       = 0x2000;
	public static final int BITPLANE_MOUNTAIN    = 0x1000;
	public static final int MASK_BITPLANES       = ~(BITPLANE_RIVER | BITPLANE_OCEAN | BITPLANE_MOUNTAIN);
	
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
	public BiomeDataOracle(MapgenV7Params params, BiomeProfileSelection biomeProfileSelection, long seed) throws InvalidNoiseParamsException {
		//this.seed = (int)(seed & 0xFFFFFFFFL);
		this.seed = (int)seed;		
		this.params = params;
		
		if (biomeProfileSelection == null) {
			AmidstLogger.warn("BiomeDataOracle running with default BiomeProfile which is not connected with the biomes the GUI will display");
			this.biomeProfile = (MinetestBiomeProfileImpl)MinetestBiomeProfileImpl.getDefaultProfiles()
					.iterator()
					.next();
		} else {
			this.biomeProfile = biomeProfileSelection.getCurrentBiomeProfile();
			biomeProfileSelection.addUpdateListener(this);			
		}
		
		// This is to avoid a divide-by-zero.
		// Parameter will be saved to map_meta.txt in limited form.
		params.float_mount_height = Math.max(params.float_mount_height, 1.0f);
		float_mount_height = params.float_mount_height;
		
		// 2D noise
		// TODO: Set these to the fragment size, rather than minetest's chunklength, and use the faster noise funcs (set scale appropriately)
		noise_terrain_base    = new Noise(params.np_terrain_base,    this.seed, params.chunk_length_x, params.chunk_length_z);
		noise_terrain_alt     = new Noise(params.np_terrain_alt,     this.seed, params.chunk_length_x, params.chunk_length_z);
		noise_terrain_persist = new Noise(params.np_terrain_persist, this.seed, params.chunk_length_x, params.chunk_length_z);
		noise_height_select   = new Noise(params.np_height_select,   this.seed, params.chunk_length_x, params.chunk_length_z);
		//noise_filler_depth    = new Noise(params.np_filler_depth,    this.seed, params.chunk_length_x, params.chunk_length_z);

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
	
	public void onBiomeProfileUpdate(BiomeProfile newBiomeProfile) {
		this.biomeProfile = newBiomeProfile;
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
	
	
	MinetestBiome calcBiomeAtPoint(MinetestBiome[] biomes, int x, int y, int z)
	{
		float heat =
			Noise.NoisePerlin2D(params.np_heat,           x, z, seed) +
			Noise.NoisePerlin2D(params.np_heat_blend,     x, z, seed);
		float humidity =
			Noise.NoisePerlin2D(params.np_humidity,       x, z, seed) +
			Noise.NoisePerlin2D(params.np_humidity_blend, x, z, seed);

		return calcBiomeFromNoise(biomes, heat, humidity, y);
	}	

	MinetestBiome calcBiomeFromNoise(MinetestBiome[] biomes, float heat, float humidity, int y)
	{
		MinetestBiome biome_closest = null;
		MinetestBiome biome_closest_blend = null;
		float dist_min = Float.MAX_VALUE;
		float dist_min_blend = Float.MAX_VALUE;

		for (short i = (short)(biomes.length - 1); i >= 0; i--) {
			MinetestBiome b = biomes[i];
			if (y > b.y_max + b.vertical_blend || y < b.y_min)
				continue;

			float d_heat = heat - b.heat_point;
			float d_humidity = humidity - b.humidity_point;
			float dist = (d_heat * d_heat) + (d_humidity * d_humidity);

			if (y <= b.y_max) { // Within y limits of biome b
				if (dist < dist_min) {
					dist_min = dist;
					biome_closest = b;
				}
			/* skip vertical blending, map doesn't need it
			} else if (dist < dist_min_blend) { // Blend area above biome b
				dist_min_blend = dist;
				biome_closest_blend = b;*/
			}
		}

		/* skip vertical blending, map doesn't need it
			
		// Carefully tune pseudorandom seed variation to avoid single node dither
		// and create larger scale blending patterns similar to horizontal biome
		// blend.
		mysrand(y + (heat + humidity) / 2);

		if (biome_closest_blend &&
				myrand_range(0, biome_closest_blend->vertical_blend) >=
				y - biome_closest_blend->y_max)
			return biome_closest_blend;
		*/

		return (biome_closest != null) ? biome_closest : MinetestBiome.NONE;	
	}
	
	private MinetestBiome[] getBiomeArray() {
		
		MinetestBiome[] result;
		
		if (biomeProfile == null) {
			result = new MinetestBiome[0];			
		} else {		
			Collection<IBiome> allBiomes = biomeProfile.allBiomes();
			result = new MinetestBiome[allBiomes.size()];
			int index = 0;
			for (IBiome biome: allBiomes) {
				if (biome != null && biome instanceof MinetestBiome) {
					result[index++] = (MinetestBiome)biome;
				}
			}
			if (index < result.length) {
				// One or all of the biomes are null or for a different engine than Minetest.
				// Resize the array to match any correct contents
				MinetestBiome[] resizedArray = new MinetestBiome[index];
				System.arraycopy(result, 0, resizedArray, 0, index);
				result = resizedArray;
				AmidstLogger.error("Current BiomeProfile contains biomes of wrong type for Minetest BiomeDataOracle");
			}
		}
		return result;
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
			float river_width = 0.19f; // 0.2f is the value used to block spawn, but 1.9 looks more accurate
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
						if (surface_y < params.water_level) biomeValue |= BITPLANE_OCEAN;
						
						// Add the mountains bitplane
						int surfaceOrSeaLevel = Math.max(surface_y, params.water_level);
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
						biomeValue |= calcBiomeAtPoint(biomes, world_x, surface_y, world_z).getIndex();
						
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
