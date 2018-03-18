package amidst.minetest.world.oracle;

import java.util.Collection;

import amidst.fragment.IBiomeDataOracle;
import amidst.gameengineabstraction.CoordinateSystem;
import amidst.gameengineabstraction.world.biome.IBiome;
import amidst.logging.AmidstLogger;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.MinetestBiomeProfileImpl;
import amidst.minetest.world.mapgen.Noise;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.settings.biomeprofile.BiomeProfileUpdateListener;

public abstract class MinetestBiomeDataOracle implements IBiomeDataOracle, BiomeProfileUpdateListener {
	protected final int seed;
	protected final MapgenParams params;
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
