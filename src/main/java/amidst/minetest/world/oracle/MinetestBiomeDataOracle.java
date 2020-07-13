package amidst.minetest.world.oracle;

import java.util.Collection;

import amidst.fragment.IBiomeDataOracle;
import amidst.gameengineabstraction.CoordinateSystem;
import amidst.gameengineabstraction.world.biome.IBiome;
import amidst.logging.AmidstLogger;
import amidst.minetest.world.mapgen.ClimateHistogram;
import amidst.minetest.world.mapgen.IHistogram2D;
import amidst.minetest.world.mapgen.IHistogram2DTransformationProvider;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.MinetestBiomeProfileImpl;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.settings.biomeprofile.BiomeProfileUpdateListener;

public abstract class MinetestBiomeDataOracle implements IBiomeDataOracle, BiomeProfileUpdateListener {
	protected final int seed;
	protected MapgenParams params;
	protected ClimateHistogram climateHistogram;
	/**
	 * Updated by onBiomeProfileUpdate event, can be null.
	 */
	protected volatile BiomeProfile biomeProfile;
	
	public static final int BITPLANE_RIVER       = 0x4000;
	public static final int BITPLANE_OCEAN       = 0x2000;
	public static final int BITPLANE_MOUNTAIN    = 0x1000;
	public static final int MASK_BITPLANES       = ~(BITPLANE_RIVER | BITPLANE_OCEAN | BITPLANE_MOUNTAIN);


	public MinetestBiomeDataOracle(MapgenParams params, BiomeProfileSelection biomeProfileSelection, long seed) {
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
	}

	/**
	 * The same as populateArray(), but if a subclass implements this instead of overriding
	 * populateArray() then clipping to Minetest world boundaries will get handled by the superclass.
	 */
	protected abstract short populateArray_unbounded(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution);


	public short populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {

		// invoke populateArray_unbounded() then clip the results to world boundaries
		short ret = MASK_BITPLANES;

		int width = result.length;
		if (width > 0) {
			// Minetest world boundaries are X=30927, X=−30912, Z=30927 and Z=−30912
			Resolution resolution = Resolution.from(useQuarterResolution);
			int height = result[0].length;
			int left   = (int) corner.getX();
			int top    = (int) corner.getY();
			int step   = resolution.getStep();
			int right  = left + (width - 1) * step;
			int bottom = top + (height - 1) * step;

			// Use -top and -bottom because Minetest uses left-handed coordinates, while Amidst uses
			// right-handed coordinates, and we want to compare against Minetest boundaries
			top = -top;
			bottom = -bottom;

			if (right >= -30912 && left <= 30927 && top >= -30912 && bottom <= 30927) {
				ret = populateArray_unbounded(corner, result, useQuarterResolution);

				if (left < -30912 || right > 30927 || bottom < -30912 || top > 30927) {
					// part of this fragment is outside the world-bounds, erase that part
					short blank_index = (short) MinetestBiome.VOID.getIndex();
					int world_z = top;
					for (int z = 0; z < height; z++, world_z -= step) {
						int world_x = left;						
						for (int x = 0; x < width; x++, world_x += step) {
							if (world_x < -30912 || world_x > 30927 || world_z < -30912 || world_z > 30927) {
								result[x][z] = blank_index;
							}
						}
					}
				}
			} else {
				// the entire fragment is outside the world-bounds
				short blank_index = (short) MinetestBiome.VOID.getIndex();
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						result[x][y] = blank_index;
					}
				}
			}
		}
		return ret;
	}

	public MapgenParams getMapgenParams() {
		return params;
	}

	protected MinetestBiome[] getBiomeArray() {
		
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

	protected MinetestBiome calcBiomeAtPoint(MinetestBiome[] biomes, int x, int y, int z)
	{
		float heat =
			Noise.NoisePerlin2D(params.np_heat,           x, z, seed) +
			Noise.NoisePerlin2D(params.np_heat_blend,     x, z, seed);
		float humidity =
			Noise.NoisePerlin2D(params.np_humidity,       x, z, seed) +
			Noise.NoisePerlin2D(params.np_humidity_blend, x, z, seed);

		return calcBiomeFromNoise(biomes, heat, humidity, y);
	}	

	protected MinetestBiome calcBiomeFromNoise(MinetestBiome[] biomes, float heat, float humidity, int y)
	{
		MinetestBiome biome_closest = null;
		MinetestBiome biome_closest_blend = null;
		float dist_min = Float.MAX_VALUE;
		float dist_min_blend = Float.MAX_VALUE;

		short biomesArrayLength = (short)biomes.length;
		for (short i = 0; i < biomesArrayLength; i++) {
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
	
	public IHistogram2D getClimateHistogram() {
		if (climateHistogram == null) {
			climateHistogram = new ClimateHistogram(
				params.np_heat,
				params.np_heat_blend,
				params.np_humidity,
				params.np_humidity_blend
			);
		}
		return climateHistogram;		
	}
	
	/**
	 * Feel free to override this with the name of the mapgen in subclasses.
	 * (Currently it's only needed by Oracles that implement IHistogram2DTransformationProvider)
	 * Normally you could get a name from the WorldType, but in some places
	 * the worldType hasn't been stored.
	 */
	public String getName() {
		String result = this.getClass().getName();
		int pos = result.lastIndexOf("DataOracle");
		if (pos >= 0 && (pos + 10) < result.length()) {
			result = result.substring(pos + 10);
		}
		return result;
	}
	
	@Override
	public void onBiomeProfileUpdate(BiomeProfile newBiomeProfile) {
		this.biomeProfile = newBiomeProfile;
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
}
