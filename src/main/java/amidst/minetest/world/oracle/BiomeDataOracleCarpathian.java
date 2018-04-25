package amidst.minetest.world.oracle;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.Constants;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenCarpathianParams;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class BiomeDataOracleCarpathian extends MinetestBiomeDataOracle {
	private final MapgenCarpathianParams carpathianParams;
		
	private Noise noise_base;
	private Noise noise_height1;
	private Noise noise_height2;
	private Noise noise_height3;
	private Noise noise_height4;
	private Noise noise_hills_terrain;
	private Noise noise_ridge_terrain;
	private Noise noise_step_terrain;
	private Noise noise_hills;
	private Noise noise_ridge_mnt;
	private Noise noise_step_mnt;
	private Noise noise_mnt_var;
	
	private int grad_wl;
	
	/**
	 * @param mapgenCarpathianParams
	 * @param biomeProfileSelection - if null then a default biomeprofile will be used
	 * @param seed
	 * @throws InvalidNoiseParamsException
	 */
	public BiomeDataOracleCarpathian(MapgenParams mapgenCarpathianParams, BiomeProfileSelection biomeProfileSelection, long seed) {

		super(mapgenCarpathianParams, biomeProfileSelection, seed);
		
		if (params instanceof MapgenCarpathianParams) {
			carpathianParams = (MapgenCarpathianParams)params;
		} else {
			AmidstLogger.error("Error: BiomeDataOracleCarpathian cannot cast params to CarpathianParams. Using defaults instead.");
			this.params = carpathianParams = new MapgenCarpathianParams();
		}
		
		grad_wl = 1 - params.water_level;
		
		try {
			// 2D noise
			noise_base          = new Noise(carpathianParams.np_base,          this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_height1       = new Noise(carpathianParams.np_height1,       this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_height2       = new Noise(carpathianParams.np_height2,       this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_height3       = new Noise(carpathianParams.np_height3,       this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_height4       = new Noise(carpathianParams.np_height4,       this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_hills_terrain = new Noise(carpathianParams.np_hills_terrain, this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_ridge_terrain = new Noise(carpathianParams.np_ridge_terrain, this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_step_terrain  = new Noise(carpathianParams.np_step_terrain,  this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_hills         = new Noise(carpathianParams.np_hills,         this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_ridge_mnt     = new Noise(carpathianParams.np_ridge_mnt,     this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_step_mnt      = new Noise(carpathianParams.np_step_mnt,      this.seed, params.chunk_length_x, params.chunk_length_z);
			
			//// 3D terrain noise
			// 1 up 1 down overgeneration
			noise_mnt_var = new Noise(carpathianParams.np_mnt_var, this.seed, params.chunk_length_x, params.chunk_length_y + 2, params.chunk_length_z);
			
		} catch (InvalidNoiseParamsException ex) {
			AmidstLogger.error("Invalid carpathianParams from Minetest game. " + ex);
			ex.printStackTrace();
		}		
	}	
		
	// Steps function
	float getSteps(float noise)
	{
		float w = 0.5f;
		float k = (float) Math.floor(noise / w);
		float f = (noise - k * w) / w;
		float s = Math.min(2.f * f, 1.f);
		return (k + s) * w;
	}
		
	float terrainLevelAtPoint(int x, int z)
	{
		float ground      = Noise.NoisePerlin2D(noise_base.np, x, z, seed);
		float height1     = Noise.NoisePerlin2D(noise_height1.np, x, z, seed);
		float height2     = Noise.NoisePerlin2D(noise_height2.np, x, z, seed);
		float height3     = Noise.NoisePerlin2D(noise_height3.np, x, z, seed);
		float height4     = Noise.NoisePerlin2D(noise_height4.np, x, z, seed);
		float hter        = Noise.NoisePerlin2D(noise_hills_terrain.np, x, z, seed);
		float rter        = Noise.NoisePerlin2D(noise_ridge_terrain.np, x, z, seed);
		float ster        = Noise.NoisePerlin2D(noise_step_terrain.np, x, z, seed);
		float n_hills     = Noise.NoisePerlin2D(noise_hills.np, x, z, seed);
		float n_ridge_mnt = Noise.NoisePerlin2D(noise_ridge_mnt.np, x, z, seed);
		float n_step_mnt  = Noise.NoisePerlin2D(noise_step_mnt.np, x, z, seed);

		int height = -Constants.MAX_MAP_GENERATION_LIMIT;

		int searchInc = 1;
		
		for (short y = 1; y <= 200; y += searchInc) { // we're going to break out of this loop when y is close to surface_level
			float mnt_var = Noise.NoisePerlin3D(noise_mnt_var.np, x, y, z, seed);

			// Gradient & shallow seabed
			int grad = (y < params.water_level) ? grad_wl + (params.water_level - y) * 3 : 1 - y;

			// Hill/Mountain height (hilliness)
			// Java doesn't have inline functions, so expanding getLerp()
			//     getLerp(float noise1, float noise2, float mod) =  noise1 + mod * (noise2 - noise1);
			// was:
			//     float hill1 = getLerp(height1, height2, mnt_var);
			//     float hill2 = getLerp(height3, height4, mnt_var);
			//     float hill3 = getLerp(height3, height2, mnt_var);
			//     float hill4 = getLerp(height1, height4, mnt_var);
			float hill1 = height1 + mnt_var * (height2 - height1);
			float hill2 = height3 + mnt_var * (height4 - height3);
			float hill3 = height3 + mnt_var * (height2 - height3);
			float hill4 = height1 + mnt_var * (height4 - height1);			
			float hilliness = Math.max(Math.min(hill1, hill2), Math.min(hill3, hill4));

			// Rolling hills
			float hill_mnt = hilliness * (float)Math.pow(n_hills, 2.f);
			float hills = (float)Math.pow(hter, 3.f) * hill_mnt;

			// Ridged mountains
			float ridge_mnt = hilliness * (1.f - Math.abs(n_ridge_mnt));
			float ridged_mountains = (float)Math.pow(rter, 3.f) * ridge_mnt;

			// Step (terraced) mountains
			float step_mnt = hilliness * getSteps(n_step_mnt);
			float step_mountains = (float)Math.pow(ster, 3.f) * step_mnt;

			// Final terrain level
			float mountains = hills + ridged_mountains + step_mountains;
			float surface_level = ground + mountains + grad;

			height = (int)surface_level;
			if ((y + (searchInc / 2)) >= surface_level) {
				// Close enough.
				// (should be properly accurate at water level, but will lose accuracy with height)
				break;
			}
			if (y > 2 && searchInc < 12) searchInc += 2;
		}

		return height;
	}
		
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
						if (surface_y < carpathianParams.water_level) biomeValue |= BITPLANE_OCEAN;
																		
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
