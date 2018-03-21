package amidst.minetest.world.oracle;

import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MapgenV5Params;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfileSelection;

public class BiomeDataOracleV5 extends MinetestBiomeDataOracle {

	private MapgenV5Params v5params = null;
	
	private Noise noise_factor;
	private Noise noise_height;
	private Noise noise_ground;
	
	
	public BiomeDataOracleV5(MapgenParams params, BiomeProfileSelection biomeProfileSelection, long seed) {
		super(params, biomeProfileSelection, seed);
		try {
			if (params instanceof MapgenV5Params) {
				v5params = (MapgenV5Params)params;
			} else {
				AmidstLogger.error("Error: BiomeDataOracleV5 cannot cast params to v5params. Using defaults instead.");
				this.params = v5params = new MapgenV5Params();
			}
			
			noise_factor = new Noise(v5params.np_factor, this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_height = new Noise(v5params.np_height, this.seed, params.chunk_length_x, params.chunk_length_z);
	
			// 3D terrain noise
			// 1-up 1-down overgeneration
			noise_ground = new Noise(v5params.np_ground, this.seed, params.chunk_length_x, params.chunk_length_y, params.chunk_length_z);
		} catch (InvalidNoiseParamsException ex) {
			AmidstLogger.error("Invalid v5params from Minetest game. " + ex);
			ex.printStackTrace();			
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
			
			/* part of (faster) version of oceans not working yet
			noise_factor.perlinMap2D(left, top, null);
			noise_height.perlinMap2D(left, top, null);
			noise_ground.perlinMap3D(left, 1, top, null);
			*/
			
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
						
						biomeValue = 0;

						//float f = 0.55f + noise_factor._result[index2d];
						float f = 0.55f + Noise.NoisePerlin2D(noise_factor.np, world_x, world_z, seed);						
						if (f < 0.01) {
							f = 0.01f;
						} else if (f >= 1.0) {
							f *= 1.6f;
						}
						//float h = noise_height._result[index2d];
						float h = Noise.NoisePerlin2D(noise_height.np, world_x, world_z, seed);						
						
						// boolean isOcean = (noise_ground._result[index3d] * f < - h)
						boolean isOcean = (Noise.NoisePerlin3D(noise_ground.np, world_x, params.water_level, world_z, seed) * f < params.water_level - h);
						
						// Very roughly calculate the surface height
						int surface_y;
						if (isOcean) {
							biomeValue |= BITPLANE_OCEAN;
							// pick a value lower than a normal beach biome
							surface_y = -10;
						} else {
							// figure out exact height, so that beaches are drawn
							// TODO binary search
							int testFromHeight = params.water_level + 6;
							if (Noise.NoisePerlin3D(noise_ground.np, world_x, testFromHeight, world_z, seed) * f < testFromHeight - h) {
								// found air, so surface_y is less than testFromHeight
								surface_y = testFromHeight - 1;
								for (int i = params.water_level + 1; i < testFromHeight; i++) {
									if (Noise.NoisePerlin3D(noise_ground.np, world_x, i, world_z, seed) *f < i - h) {
										// found air
										surface_y = i - 1;
										break;
									}
								}
							} else {
								// pick a value higher than a normal beach biome
								surface_y = +10;								
							}							
						}						
						
						// Add the biome index.
						// (noise_height 'offset' is the average level of terrain. At least 50% of
						// terrain will be below this)						
						biomeValue |= calcBiomeAtPoint(biomes, world_x, surface_y, world_z).getIndex();						
						
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
