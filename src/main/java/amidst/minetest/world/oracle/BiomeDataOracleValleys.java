package amidst.minetest.world.oracle;

import javax.vecmath.Point2d;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.IHistogram2D;
import amidst.minetest.world.mapgen.IHistogram2DTransformationProvider;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MapgenValleysParams;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class BiomeDataOracleValleys extends MinetestBiomeDataOracle implements IHistogram2DTransformationProvider {
	private final MapgenValleysParams valleysParams;
		
	/** Raising this reduces the rate of evaporation */
	static final float evaporation = 300.0f;
	static final float humidity_dropoff = 4.0f;
	/** Constant to convert altitude chill to heat */
	static final float alt_to_heat = 20.0f;
	/** Humidity reduction by altitude */
	static final float alt_to_humid = 10.0f;
	
	
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
	
	boolean humid_rivers;
	boolean use_altitude_chill;
	boolean use_altitude_dry;
	boolean vary_driver_depth;
	
	float altitude_chill;
	float river_depth_bed;
	float river_size_factor;	
	
	/**
	 * Reusable instance of TerrainNoise, to save unnecessary construction/mem-fragmentation 
	 */
	TerrainNoise tempTerrainNoise = new TerrainNoise();
		
	class TerrainNoise {
		int x;
		int z;
		float terrain_height;
		float rivers;
		float valley;
		float valley_profile;
		float slope;
		float inter_valley_fill;
		
		float heat;
		float humidity;
		boolean isRiver;
	};
	
	/**
	 * Provides the climate histogram for a Valleys map at the given altitude
	 * (Valleys mapgen adjusts the climate with altitude and around rivers)
	 */
	class ValleysClimateHistogram implements IHistogram2D {
		IHistogram2D sourceHistogram;
		float altitude;
		Point2d sampleMean = null;		
		
		@Override
		public double frequencyOfOccurance(float temperature, float humidity) {

			if (altitude > 0.0f) {
				if (use_altitude_dry)   humidity    += alt_to_humid * altitude / altitude_chill;
				if (use_altitude_chill) temperature += alt_to_heat  * altitude / altitude_chill;			
			}
			
			// TODO Crazy math to add river humidity distribution to sourceHistogram

			// Average heat was increased to balance against altitude chill.
			if (use_altitude_chill) temperature -= 5.0f;
			// Humidity was scaled down to balance the effect of rivers.
			if (humid_rivers) humidity /= 0.8f;	

			// Now that we've performed the opposite of every transformation Valleys mapgen
			// makes to the humidity and temperature, we can obtain the frequencyOfOccurance.
			double result = sourceHistogram.frequencyOfOccurance(temperature, humidity);
			
			// results outside the sampling range return NaN rather than zero, but the
			// sampling range covers all non-zero values, so we can treat NaN as zero.
			return Double.isNaN(result) ? 0 : result;
		}

		@Override
		/**
		 * Returns the "FrequencyOfOccurance" value at which 'percentile' amount of
		 * samples will fall beneath.
		 * So if percentile was 10, then a value between 0 and 1 would be returned such
		 * that 10% of results from FrequencyOfOccurance() would fall below it.
		 */
		public double frequencyAtPercentile(double percentile) {
			// TODO Auto-generated method stub
			return sourceHistogram.frequencyAtPercentile(percentile);
		}

		@Override
		public Point2d getSampleMean() {
			if (sampleMean == null) { 
				sampleMean = new Point2d(sourceHistogram.getSampleMean());
				
				// Average heat was increased to balance against altitude chill.
				if (use_altitude_chill) sampleMean.x += 5.0f;
				// Humidity was scaled down to balance the effect of rivers.
				if (humid_rivers) sampleMean.y *= 0.8f;	
							
				// TODO Crazy math to add river humidity distribution to sourceHistogram
				
				if (altitude > 0.0f) {
					if (use_altitude_dry)   sampleMean.y -= alt_to_humid * altitude / altitude_chill;
					if (use_altitude_chill) sampleMean.x -= alt_to_heat  * altitude / altitude_chill;			
				}
			}
			return sampleMean;
		}

		public ValleysClimateHistogram(IHistogram2D source_histogram, float altitude) {
			this.sourceHistogram = source_histogram;
			this.altitude        = altitude;			
		}		
	}
		
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
		
		altitude_chill    = valleysParams.altitude_chill;
		river_depth_bed   = valleysParams.river_depth + 1.0f;
		river_size_factor = valleysParams.river_size / 100.0f;
		
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
		
		humid_rivers       = (valleysParams.spflags & MapgenValleysParams.FLAG_VALLEYS_HUMID_RIVERS)     > 0;
		use_altitude_chill = (valleysParams.spflags & MapgenValleysParams.FLAG_VALLEYS_ALT_CHILL)        > 0;
		use_altitude_dry   = (valleysParams.spflags & MapgenValleysParams.FLAG_VALLEYS_ALT_DRY)          > 0;
		vary_driver_depth  = (valleysParams.spflags & MapgenValleysParams.FLAG_VALLEYS_VARY_RIVER_DEPTH) > 0;
	}	
	
	@Override
	public IHistogram2D getTransformedHistogram(IHistogram2D climate_histogram, float altitude) {
		return new ValleysClimateHistogram(climate_histogram, altitude);
	}	
		
	float terrainLevelAtPoint(int x, int z)
	{
		tempTerrainNoise.heat =
				Noise.NoisePerlin2D(params.np_heat,           x, z, seed) +
				Noise.NoisePerlin2D(params.np_heat_blend,     x, z, seed);
		tempTerrainNoise.humidity =
				Noise.NoisePerlin2D(params.np_humidity,       x, z, seed) +
				Noise.NoisePerlin2D(params.np_humidity_blend, x, z, seed);
			
		// Altitude chill tends to reduce the average heat.
		if (use_altitude_chill) tempTerrainNoise.heat += 5.0f;
		// River humidity tends to increase the humidity range.
		if (humid_rivers) tempTerrainNoise.humidity *= 0.8f;		
		
		tempTerrainNoise.x                 = x;
		tempTerrainNoise.z                 = z;
		tempTerrainNoise.terrain_height    = Noise.NoisePerlin2D(noise_terrain_height.np, x, z, seed);
		tempTerrainNoise.rivers            = Noise.NoisePerlin2D(noise_rivers.np, x, z, seed);
		tempTerrainNoise.valley            = Noise.NoisePerlin2D(noise_valley_depth.np, x, z, seed);
		tempTerrainNoise.valley_profile    = Noise.NoisePerlin2D(noise_valley_profile.np, x, z, seed);
		tempTerrainNoise.slope             = Noise.NoisePerlin2D(noise_inter_valley_slope.np, x, z, seed);
		tempTerrainNoise.inter_valley_fill = 0.f;
		
		float terrain_height = adjustedTerrainLevelFromNoise(tempTerrainNoise);
		
		// Note that tempTerrainNoise.slope, tempTerrainNoise.rivers, and 
		// tempTerrainNoise.valley have now been updated with new values.
		//
		// If there is a river then terrain_height is the height of the bottom of the
		// river, otherwise it's the height above the water-table.
		// tempTerrainNoise.rivers is the water table (and the height of rivers), it's 
		// greater than terrain_height if there is a river.
		
		// Ground height ignoring riverbeds
		float t_alt = Math.max(tempTerrainNoise.rivers, terrain_height);
		
		if (humid_rivers) {			
			float river_y = tempTerrainNoise.rivers;
			if (vary_driver_depth) {
				// get a heat value that includes altitude chill
				float heat = (use_altitude_chill && (terrain_height > 0.0f || river_y > 0.0f)) ?
						tempTerrainNoise.heat - alt_to_heat * Math.max(terrain_height, river_y) / altitude_chill : 
						tempTerrainNoise.heat;
				float delta = tempTerrainNoise.humidity - 50.0f;
				if (delta < 0.0f) {
					float t_evap = (heat - 32.0f) / evaporation;
					river_y += delta * Math.max(t_evap, 0.08f);
				}				
			}			
			float water_depth = (t_alt - river_y) / humidity_dropoff; // depth of water-table from surface, not depth of the river
			double humidityScale = 1.0f + Math.pow(0.5f, Math.max(water_depth, 1.0f));
			tempTerrainNoise.humidity *= humidityScale;
			//AmidstLogger.info("*" + humidityScale);
			
		}
		if (use_altitude_dry) {
			if (t_alt > 0.0f) tempTerrainNoise.humidity -= alt_to_humid * t_alt / altitude_chill;
		}
		if (use_altitude_chill) {
			if (t_alt > 0.0f) tempTerrainNoise.heat -= alt_to_heat * t_alt / altitude_chill;			
		}
		
		return terrain_height;
	}
	
	/**
	 * Side effect warning: Updates tn.slope, tn.rivers, tn.valley
	 *
	 * This avoids duplicating the code in terrainLevelFromNoise, adding
	 * only the final step of terrain generation without a noise map.
	 */
	float adjustedTerrainLevelFromNoise(TerrainNoise tn)
	{
		float mount = terrainLevelFromNoise(tn);
		int y_start = (int)(mount < 0.f ? (mount - 0.5f) : (mount + 0.5f)); // was "myround(muount);", s32 myround(f32 f) { return (s32)(f < 0.f ? (f - 0.5f) : (f + 0.5f)); }

		for (int y = y_start; y <= y_start + 1000; y++) {
			float fill = Noise.NoisePerlin3D(noise_inter_valley_fill.np, tn.x, y, tn.z, seed);
			if (fill * tn.slope < y - mount) {
				mount = Math.max(y - 1, mount);   // was using MYMAX(),, #define MYMAX(a, b) ((a) > (b) ? (a) : (b))
				break;
			}
		}
		return mount;
	}	
		
	/**
	 * Side effect warning: Updates tn.slope, tn.rivers, tn.valley
	 * 
	 * This is in a separate function to save the code inside minetest from having
	 * to maintain two similar sets of complicated code to determine ground level.  
	 * @param tn
	 * @return
	 */
	float terrainLevelFromNoise(TerrainNoise tn)
	{
		// The square function changes the behaviour of this noise:
		//  very often small, and sometimes very high.
		float valley_d = tn.valley * tn.valley;  // was using MYSQUARE(), #define MYSQUARE(x) (x) * (x)
	
		// valley_d is here because terrain is generally higher where valleys
		//  are deep (mountains). base represents the height of the
		//  rivers, most of the surface is above.
		float base = tn.terrain_height + valley_d;

		// "river" represents the distance from the river
		float riverDist = Math.abs(tn.rivers) - river_size_factor;

		// Use the curve of the function 1-exp(-(x/a)^2) to model valleys.
		// "valley" represents the height of the terrain, from the rivers.
		float tv = Math.max(riverDist / tn.valley_profile, 0.0f);
		tn.valley = valley_d * (1.0f - (float)Math.exp(-(tv * tv)));

		// Approximate height of the terrain at this point
		float mount = base + tn.valley;

		tn.slope *= tn.valley;

		// Base ground is returned as rivers since it's basically the water table.
		tn.rivers = base;

		// Rivers are placed where "river" is negative, so where the original noise
		// value is close to zero.
		if (riverDist < 0.0f) {
			tn.isRiver = true;
			
			// Use the the function -sqrt(1-x^2) which models a circle
			float tr = riverDist / river_size_factor + 1.0f;
			float depth = (float) (river_depth_bed *
				Math.sqrt(Math.max(0.0f, 1.0f - (tr * tr))));

			// base - depth : height of the bottom of the river
			// water_level - 3 : don't make rivers below 3 nodes under the surface.
			// We use three because that's as low as the swamp biomes go.
			// There is no logical equivalent to this using rangelim.
			mount =
				Math.min(Math.max(base - depth, (float)(params.water_level - 3)), mount);

			// Slope has no influence on rivers
			tn.slope = 0.0f;
		} else {
			tn.isRiver = false;
		}

		return mount;
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
						if (surface_y < valleysParams.water_level) biomeValue |= BITPLANE_OCEAN;
						if (tempTerrainNoise.isRiver) biomeValue |= BITPLANE_RIVER;
						//if (isMountains) biomeValue |= BITPLANE_MOUNTAIN;
																		
						// add the biome index
						// (mask the bitplanes in case the biome returned is -1 (NONE)
						biomeValue |= calcBiomeFromNoise(biomes, tempTerrainNoise.heat, tempTerrainNoise.humidity, surface_y).getIndex() & MASK_BITPLANES;
						
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
