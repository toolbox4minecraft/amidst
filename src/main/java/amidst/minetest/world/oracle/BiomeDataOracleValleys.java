package amidst.minetest.world.oracle;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.Constants;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MapgenValleysParams;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class BiomeDataOracleValleys extends MinetestBiomeDataOracle {
	private final MapgenValleysParams valleysParams;
		
	private Noise noise_cave1;
	private Noise noise_cave2;
	private Noise noise_filler_depth;
	private Noise noise_inter_valley_fill;
	private Noise noise_inter_valley_slope;
	private Noise noise_rivers;
	private Noise noise_massive_caves;
	private Noise noise_terrain_height;
	private Noise noise_valley_depth;
	private Noise noise_valley_profile;
	private int grad_wl;
	
	boolean humid_rivers;
	boolean use_altitude_chill;
	float humidity_adjust;
	
	
	/**
	 * @param mapgenCarpathianParams
	 * @param biomeProfileSelection - if null then a default biomeprofile will be used
	 * @param seed
	 * @throws InvalidNoiseParamsException
	 */
	public BiomeDataOracleValleys(MapgenParams mapgenValleysParams, BiomeProfileSelection biomeProfileSelection, long seed) {

		super(mapgenValleysParams, biomeProfileSelection, seed);
		
		if (params instanceof MapgenValleysParams) {
			valleysParams = (MapgenValleysParams)params;
		} else {
			AmidstLogger.error("Error: BiomeDataOracleCarpathian cannot cast params to CarpathianParams. Using defaults instead.");
			this.params = valleysParams = new MapgenValleysParams();
		}
		
		grad_wl = 1 - params.water_level;
		
		try {
			// 2D noise
			noise_filler_depth       = new Noise(valleysParams.np_filler_depth,       this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_inter_valley_slope = new Noise(valleysParams.np_inter_valley_slope, this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_rivers             = new Noise(valleysParams.np_rivers,             this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_terrain_height     = new Noise(valleysParams.np_terrain_height,     this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_valley_depth       = new Noise(valleysParams.np_valley_depth,       this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_valley_profile     = new Noise(valleysParams.np_valley_profile,     this.seed, params.chunk_length_x, params.chunk_length_z);
			
			//// 3D terrain noise
			// 1 up 1 down overgeneration
			noise_inter_valley_fill = new Noise(valleysParams.np_inter_valley_fill, this.seed, params.chunk_length_x, params.chunk_length_y + 2, params.chunk_length_z);
			// 1-down overgeneraion
			noise_cave1             = new Noise(valleysParams.np_cave1,             this.seed, params.chunk_length_x, params.chunk_length_y + 1, params.chunk_length_z);
			noise_cave2             = new Noise(valleysParams.np_cave2,             this.seed, params.chunk_length_x, params.chunk_length_y + 1, params.chunk_length_z);
			noise_massive_caves     = new Noise(valleysParams.np_massive_caves,     this.seed, params.chunk_length_x, params.chunk_length_y + 1, params.chunk_length_z);
			
		} catch (InvalidNoiseParamsException ex) {
			AmidstLogger.error("Invalid valleysParams from Minetest game. " + ex);
			ex.printStackTrace();
		}
		
		humid_rivers       = (valleysParams.spflags & MapgenValleysParams.FLAG_VALLEYS_HUMID_RIVERS) > 0;
		use_altitude_chill = (valleysParams.spflags & MapgenValleysParams.FLAG_VALLEYS_ALT_CHILL) > 0;
	}	
		
	float terrainLevelAtPoint(int x, int z)
	{
		TerrainNoise tn;

		float rivers = NoisePerlin2D(&noise_rivers->np, x, z, seed);
		float valley = NoisePerlin2D(&noise_valley_depth->np, x, z, seed);
		float inter_valley_slope = NoisePerlin2D(&noise_inter_valley_slope->np, x, z, seed);

		tn.x                 = x;
		tn.z                 = z;
		tn.terrain_height    = NoisePerlin2D(&noise_terrain_height->np, x, z, seed);
		tn.rivers            = &rivers;
		tn.valley            = &valley;
		tn.valley_profile    = NoisePerlin2D(&noise_valley_profile->np, x, z, seed);
		tn.slope             = &inter_valley_slope;
		tn.inter_valley_fill = 0.f;

		return adjustedTerrainLevelFromNoise(&tn);	}
		
	@Override
	public short populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		
		// The Carpathian mapgen terrain is not yet stable.
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
				for (int y = 0; y < height; y++) {
					
					world_z = top + (y << shift);
					world_x = left;
					
					// Use -world_z because Minetest uses left-handed coordinates, while Minecraft 
					// and Amidst use right-handed coordinates.
					world_z = -world_z;
					
					for (int x = 0; x < width; x++) {
						
						biomeValue = 0;

						// Add the ocean bitplane
						int surface_y = (int)terrainLevelAtPoint(world_x, world_z);
						if (surface_y < valleysParams.water_level) biomeValue |= BITPLANE_OCEAN;
						if (isMountains) biomeValue |= BITPLANE_MOUNTAIN;
																		
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
}
