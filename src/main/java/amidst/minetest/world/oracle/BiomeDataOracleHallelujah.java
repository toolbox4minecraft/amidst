package amidst.minetest.world.oracle;

import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.Constants;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenHallelujahParams;
import amidst.minetest.world.mapgen.MapgenHallelujahParams.CoreSize;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.minetest.world.mapgen.PcgRandom;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class BiomeDataOracleHallelujah extends MinetestBiomeDataOracle {
	private final MapgenHallelujahParams hallelujahParams;
			
	class Core{
		int x;
		int y;
		int z;
		float radius;
		float depth;
		CoreSize type;
		Boolean marked = null;
		
		public Core(int x, int y, int z, float radius, float depth, CoreSize type) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.radius = radius;
			this.depth = depth;
			this.type = type;
		}
	}
	
	private Noise noise_density;
	private Noise noise_eddyField;
	private Noise noise_surfaceMap;
	
	/**
	 * @param mapgenHallelujahParams
	 * @param biomeProfileSelection - if null then a default biomeprofile will be used
	 * @param seed
	 * @throws InvalidNoiseParamsException
	 */
	public BiomeDataOracleHallelujah(MapgenParams mapgenHallelujahParams, BiomeProfileSelection biomeProfileSelection, long seed) {

		super(mapgenHallelujahParams, biomeProfileSelection, seed);
		
		if (params instanceof MapgenHallelujahParams) {
			hallelujahParams = (MapgenHallelujahParams)params;
		} else {
			AmidstLogger.error("Error: BiomeDataOracleHallelujah cannot cast params to HallelujahParams. Using defaults instead.");
			this.params = hallelujahParams = new MapgenHallelujahParams();
		}
		
		try {
			// 2D noise
			noise_eddyField     = new Noise(hallelujahParams.np_eddyField,     this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_surfaceMap     = new Noise(hallelujahParams.np_surfaceMap,     this.seed, params.chunk_length_x, params.chunk_length_z);

			// 3D noise
			noise_density       = new Noise(hallelujahParams.np_density,       this.seed, params.chunk_length_x, params.chunk_length_y, params.chunk_length_z);			
		} catch (InvalidNoiseParamsException ex) {
			AmidstLogger.error("Invalid HallelujahParams from Minetest game. " + ex);
			ex.printStackTrace();
		}		
	}	

	private List<Core> findCores(List<Core> previous, CoreSize size, int x1, int z1, int x2, int z2) {
		
		List<Core> result = new ArrayList<Core>();

		PcgRandom prng;
		List<Core> coresInTerritory = new ArrayList<Core>();
		
		Noise noise = noise_eddyField;
		
		float rotA = (float) Math.cos(-15 * Math.PI/180f); 				
		float rotB = (float) Math.sin(-15 * Math.PI/180f); 
		
		for(int z = (int)Math.floor(z1 / (float)size.territorySize); z <= Math.floor(z2 / (float)size.territorySize); z++) {
			for(int x = (int)Math.floor(x1 / (float)size.territorySize); x <= Math.floor(x2 / (float)size.territorySize); x++) {
				
				coresInTerritory.clear();
				prng = new PcgRandom(
					x * 8973896 +
					z * 7467838 +
					seed + 9438						
				);							
				
				for(int i = 0; i < size.coresPerTerritory; i++) {
					int coreX = x * size.territorySize + prng.range(0, size.territorySize - 1);
					int coreZ = z * size.territorySize + prng.range(0, size.territorySize - 1);
				
					float noiseX = rotA * coreX - rotB * coreZ;
					float noiseZ = rotB * coreX + rotA * coreZ;
					float tendrils     = Noise.NoisePerlin2D(noise.np, noiseX, noiseZ, seed);
					
					if (Math.abs(tendrils) < size.frequency) {
						
						boolean nexus = !size.requiresNexus;
						if (!nexus) {
							float field1b     = Noise.NoisePerlin2D(noise.np, noiseX + 2, noiseZ, seed);
							float field1c     = Noise.NoisePerlin2D(noise.np, noiseX, noiseZ + 2, seed);
							if (Math.abs(tendrils - field1b) + Math.abs(tendrils - field1c) < 0.02)  nexus = true;
						}

						if (nexus) {
							float radius    = (size.maxRadius + prng.range(0, size.maxRadius) * 2) / 3;
							float depth     = (size.maxRadius + prng.range(0, size.maxRadius) * 2) / 2;
							int thickness   =  size.maxRadius + prng.range(0, size.maxThickness); // only needed to advance the PRNG
						
							if (coreX >= x1 && coreX < x2 & coreZ >= z1 && coreZ < z2) {
								
								boolean spaceAvailable = !size.exclusive;
								if (!spaceAvailable) {
									// see if any other cores occupy this space, and if so then 
									// either deny the core, or raise it
									spaceAvailable = true;
									float minDistSquared = radius * radius * .7f;
									for(Core core: previous) {
										if ((core.x - coreX)*(core.x - coreX) + (core.z - coreZ)*(core.z - coreZ) <=  (minDistSquared + core.radius * core.radius)) {
											spaceAvailable = false;
											break;
										}
									}
									if (spaceAvailable) for(Core core: coresInTerritory) {
										if ((core.x - coreX)*(core.x - coreX) + (core.z - coreZ)*(core.z - coreZ) <=  (minDistSquared + core.radius * core.radius)) {
											spaceAvailable = false;
											break;
										}
									}
								}
								
								if (spaceAvailable) {
									Core newCore = new Core(
										coreX, 
										hallelujahParams.cloudlands_altitude, 
										coreZ, 
										radius,
										depth,
										size
									);
									result.add(newCore);
									coresInTerritory.add(newCore);
								}
							} else {
								// We couldn't filter this core out earlier as that would break the determinism of the prng 
							}
						}
					}					
				}
			}			
		}
		return result;
	}
	
	
	private List<Core> getCores(int x1, int z1, int x2, int z2) {
		
		List<Core> result = new ArrayList<Core>();
		for(CoreSize size: hallelujahParams.cores) {
			result.addAll(findCores(result, size, x1 - size.maxRadius, z1 - size.maxRadius, x2 + size.maxRadius, z2 + size.maxRadius));
		}		
		return result;
	}
		
	@Override
	public short populateArray_unbounded(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		
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
			
			List<Core> cores = getCores(left, -(top + (height << shift)), left + (width << shift), -top); 
			
			try {			
				for (int y = 0; y < height; y++) {
					
					world_z = top + (y << shift);
					world_x = left;
					
					// Use -world_z because Minetest uses left-handed coordinates, while Minecraft 
					// and Amidst use right-handed coordinates.
					world_z = -world_z;
					
					for (int x = 0; x < width; x++) {
						
						biomeValue = 0;
						short surface_y = Constants.MAX_MAP_GENERATION_LIMIT;

						/* visualizations used in the design of the mod
						float rotX = rotA * world_x - rotB * world_z;
						float rotZ = rotB * world_x + rotA * world_z;												
						float field1  = Noise.NoisePerlin2D(noise_eddyField.np, rotX, rotZ, seed);
						float field1b = Noise.NoisePerlin2D(noise_eddyField.np, rotX + 2, rotZ, seed);
						float field1c = Noise.NoisePerlin2D(noise_eddyField.np, rotX, rotZ + 2, seed);
						if (Math.abs(field1 - field1b) + Math.abs(field1 - field1c) < 0.01)  biomeValue |= BITPLANE_MOUNTAIN;
						if (Math.abs(field1) <= .1) {
							biomeValue |= BITPLANE_RIVER;
						} */
						
						for (Core core : cores) {
							
							float distanceSquared = (world_x - core.x) * (world_x - core.x) + (world_z - core.z) * (world_z - core.z);
							float radiusSquared = core.radius * core.radius;
							if (distanceSquared <= radiusSquared) {
								float horz_easing;
								float noise_weighting = 1.0f;
								int shapeType = (int)Math.floor(core.depth + core.radius + core.x) % 5;
								if (shapeType < 2) {
									horz_easing = 1 - distanceSquared / radiusSquared;
								} else if (shapeType == 2) {
									horz_easing =  (float)(1.0f - Math.sqrt(distanceSquared) / core.radius);									
								} else {
						            float squared  = 1 - distanceSquared / radiusSquared;
						            float distance = (float) Math.sqrt(distanceSquared);
				                    float distance_normalized = distance / core.radius;
				                    float root = (float) (1 - Math.sqrt(distance) / Math.sqrt(core.radius));
				                    horz_easing = (float) Math.min(1, 0.8*distance_normalized*squared + 1.2*(1-distance_normalized)*root);
				                    noise_weighting = 0.63f;							
								}
								if (core.radius + core.depth > 80) {									
								    if (core.radius + core.depth > 120) { 
								      noise_weighting = 0.35f;
								    } else {
								      noise_weighting = Math.min(0.6f, noise_weighting);
								    }
								}								
													
								// sample density at two points to reduce holes in our island maps
								float density1 = Noise.NoisePerlin3D(noise_density.np, world_x, 1, world_z, seed);
								float density2 = Noise.NoisePerlin3D(noise_density.np, world_x, -3, world_z, seed);
					            density1 = noise_weighting * density1 + (1 - noise_weighting) * hallelujahParams.np_density.offset;
					            density2 = noise_weighting * density2 + (1 - noise_weighting) * hallelujahParams.np_density.offset;
								float maxDensity = Math.max(density1, density2);

								if (maxDensity * (horz_easing + 1) / 2 > hallelujahParams.required_density) {
									
									if (Noise.NoisePerlin2D(noise_surfaceMap.np, world_x, world_z, seed) < 0) {
										if (density1 * (horz_easing + 1) / 2 > hallelujahParams.required_density + core.type.pondWallBuffer) {
											biomeValue |= BITPLANE_OCEAN;											
										}
									}
									surface_y = hallelujahParams.cloudlands_altitude;
									
									if ((biomeValue & MASK_BITPLANES) == 0) {
										// (mask the bitplanes in case the biome returned is -1 (NONE)
										biomeValue = (short)(biomeValue & ~MASK_BITPLANES);
										biomeValue |= calcBiomeAtPoint(biomes, core.x, surface_y, core.z).getIndex() & MASK_BITPLANES;
									}
								}
								
								if ((core.marked == null || core.marked.booleanValue()) && ((x + y) & 1) == 0) {
									// Mark cores (was used for tuning the mod)
									if (core.marked == null) {
										core.marked = new Boolean(false);
										int territoryZ = (int)Math.floor(core.z / (float)core.type.territorySize); 
										int territoryX = (int)Math.floor(core.x / (float)core.type.territorySize);
										
										if (((territoryX + territoryZ) % 2) == 0 && core.radius > 18 && core.depth > 20  && core.radius + core.depth > 60) {
										
											float heat =
												Noise.NoisePerlin2D(params.np_heat,           core.x, core.z, seed) +
												Noise.NoisePerlin2D(params.np_heat_blend,     core.x, core.z, seed);
											float humidity =
												Noise.NoisePerlin2D(params.np_humidity,       core.x, core.z, seed) +
												Noise.NoisePerlin2D(params.np_humidity_blend, core.x, core.z, seed);
											
											if ((heat < 5 && core.x % 3 == 0) || (heat > 50 && humidity > 60)) {
												core.marked = new Boolean(true);
											}
										}
									}
									if (core.marked.booleanValue()) biomeValue |= BITPLANE_MOUNTAIN;
								}
							}							
						}

						if (surface_y >= Constants.MAX_MAP_GENERATION_LIMIT) {
							// It's a long fall, show the clouds/void
							biomeValue = (short) MinetestBiome.VOID.getIndex();							
						}						
						
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
