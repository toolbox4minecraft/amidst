package amidst.minetest.world.oracle;

import java.io.Console;

import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.InvalidNoiseParamsException;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MapgenV6Params;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.minetest.world.mapgen.Noise;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.biomeprofile.BiomeProfileSelection;

public class BiomeDataOracleV6 extends MinetestBiomeDataOracle {

	private MapgenV6Params v6params = null;
	
	private static final int AVERAGE_MUD_AMOUNT  =   4;
	private static final int DESERT_STONE_BASE   = -32;
	private static final int ICE_BASE            =   0;
	private static final float FREQ_HOT          =   0.4f;
	private static final float FREQ_SNOW         =  -0.4f;
	private static final float FREQ_TAIGA        =   0.5f;
	private static final float FREQ_JUNGLE       =   0.5f;
	
	/**
	 * Keep in alphabetical order, as we reference them by ordinal value, and biome sets are ordered
	 * alphabetically when saved. 
	 */
	enum BiomeV6Type {
		BT_BEACH,
		BT_DESERT,
		BT_JUNGLE,
		BT_NORMAL,
		BT_TAIGA,
		BT_TUNDRA
	};	
	
	private Noise noise_terrain_base;
	private Noise noise_terrain_higher;
	private Noise noise_steepness;
	private Noise noise_height_select;
	private Noise noise_mud;
	private Noise noise_beach;
	private Noise noise_biome;
	private Noise noise_humidity;
	
	/**
	 * Annoying that Java doesn't have an efficient way to pass primitives by reference / return more than one value
	 */
	private boolean lastTerrainLevelWasMountains = false;
	
	public BiomeDataOracleV6(MapgenParams params, BiomeProfileSelection biomeProfileSelection, long seed) {
		super(params, biomeProfileSelection, seed);
		
		try {
			if (params instanceof MapgenV6Params) {
				v6params = (MapgenV6Params)params;
			} else {
				AmidstLogger.error("Error: BiomeDataOracleV6 cannot cast params to v6params. Using defaults instead.");
				this.params = v6params = new MapgenV6Params();
			}
			
			noise_terrain_base = new Noise(v6params.np_terrain_base,     this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_terrain_base   = new Noise(v6params.np_terrain_base,   this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_terrain_higher = new Noise(v6params.np_terrain_higher, this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_steepness      = new Noise(v6params.np_steepness,      this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_height_select  = new Noise(v6params.np_height_select,  this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_mud            = new Noise(v6params.np_mud,            this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_beach          = new Noise(v6params.np_beach,          this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_biome          = new Noise(v6params.np_biome,          this.seed, params.chunk_length_x, params.chunk_length_z);
			noise_humidity       = new Noise(v6params.np_humidity,       this.seed, params.chunk_length_x, params.chunk_length_z);
		} catch (InvalidNoiseParamsException ex) {
			
			AmidstLogger.error("Invalid v5params from Minetest game. " + ex);
			ex.printStackTrace();			
		}				
	}

	
	float baseTerrainLevel(float terrain_base, float terrain_higher,
			float steepness, float height_select)
	{
		float base   = 1 + terrain_base;
		float higherGround = 1 + terrain_higher;

		// Limit higher ground level to at least base
		if(higherGround < base)
			higherGround = base;

		// Steepness factor of cliffs
		float b = steepness < 0 ? 0 : (steepness > 1000f ? 1000f : steepness); // rangelim(steepness, 0.0, 1000.0);
		b = 5 * b * b * b * b * b * b * b;
		b = (b < 0.5f ? 0.5f : (b > 1000f ? 1000f : b)); // rangelim(b, 0.5, 1000.0);

		// Values 1.5...100 give quite horrible looking slopes
		if (b > 1.5 && b < 100.0) {
			b = (b < 10.0f) ? 1.5f : 100.0f;
		}

		float a_off = -0.20f; // Offset to more low
		float higherGroundWeight = 0.5f + b * (a_off + height_select);
		higherGroundWeight = (higherGroundWeight < 0.0f ? 0.0f : (higherGroundWeight > 1.0f ? 1.0f : higherGroundWeight)); // rangelim(higherGroundWeight, 0.0, 1.0); 

		//float baseHeight = base * (1.0f - higherGroundWeight);
		float additionalHeight = higherGround * higherGroundWeight;
		
		// V6 doesn't really have mountains, but we can indicate some approximation
		lastTerrainLevelWasMountains = additionalHeight > 20 && higherGroundWeight > 0.9;
		
		return base * (1.0f - higherGroundWeight) + additionalHeight;
	}
	
	float baseTerrainLevelFromNoise(int x, int z)
	{
		if ((v6params.spflags & MapgenV6Params.FLAG_V6_FLAT) > 0)	return params.water_level;

		float terrain_base   = Noise.NoisePerlin2D(noise_terrain_base.np,
								x + 0.5f * noise_terrain_base.np.spread.x, z + 0.5f * noise_terrain_base.np.spread.y, seed);
		float terrain_higher = Noise.NoisePerlin2D(noise_terrain_higher.np,
								x + 0.5f * noise_terrain_higher.np.spread.x, z + 0.5f * noise_terrain_higher.np.spread.y, seed);
		float steepness      = Noise.NoisePerlin2D(noise_steepness.np,
								x + 0.5f * noise_steepness.np.spread.x, z + 0.5f * noise_steepness.np.spread.y, seed);
		float height_select  = Noise.NoisePerlin2D(noise_height_select.np,
								x + 0.5f * noise_height_select.np.spread.x, z + 0.5f * noise_height_select.np.spread.y, seed);

		return baseTerrainLevel(terrain_base, terrain_higher, steepness, height_select);
	}
	
	protected BiomeV6Type calcBiomeAtPoint(MinetestBiome[] biomes, int x, int z)
	{
		//float d = Noise.NoisePerlin2D(noise_biome.np,
		//		x + 0.5f * noise_biome.np.spread.x, z + 0.5f * noise_biome.np.spread.y, seed);
		//float humidity = Noise.NoisePerlin2D(noise_humidity.np,
		//		x + 0.5f * noise_humidity.np.spread.x, z + 0.5f * noise_humidity.np.spread.y, seed);
		float d = Noise.NoisePerlin2D(noise_biome.np,
				x + 0.6f * noise_biome.np.spread.x, z + 0.2f * noise_biome.np.spread.y, seed);
		float humidity = Noise.NoisePerlin2D(noise_humidity.np, x, z, seed);

		if ((v6params.spflags & MapgenV6Params.FLAG_V6_SNOWBIOMES) > 0) {
			
			float blend = ((v6params.spflags & MapgenV6Params.FLAG_V6_BIOMEBLEND) > 0) ? Noise.noise2d(x, z, seed) / 40 : 0;

			if (d > FREQ_HOT + blend) {
				if (humidity > FREQ_JUNGLE + blend)
					return BiomeV6Type.BT_JUNGLE;

				return BiomeV6Type.BT_DESERT;
			}

			if (d < FREQ_SNOW + blend) {
				if (humidity > FREQ_TAIGA + blend)
					return BiomeV6Type.BT_TAIGA;

				return BiomeV6Type.BT_TUNDRA;
			}

			return BiomeV6Type.BT_NORMAL;
		}

		if (d > v6params.freq_desert)
			return BiomeV6Type.BT_DESERT;

		if (((v6params.spflags & MapgenV6Params.FLAG_V6_BIOMEBLEND) > 0) && (d > v6params.freq_desert - 0.10) &&
				((Noise.noise2d(x, z, seed) + 1.0) > (v6params.freq_desert - d) * 20.0))
			return BiomeV6Type.BT_DESERT;

		if (((v6params.spflags & MapgenV6Params.FLAG_V6_JUNGLES) > 0) && humidity > 0.75)
			return BiomeV6Type.BT_JUNGLE;

		return BiomeV6Type.BT_NORMAL;
	}	
	
	private boolean haveBeach(int x, int z) {
		
		return Noise.NoisePerlin2D(noise_beach.np,
				x + 0.2f * noise_beach.np.spread.x, z + 0.7f * noise_beach.np.spread.y, seed) > v6params.freq_beach;		
	}
	
	@Override
	public short populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		
		// The v6 mapgen has been officially stable since 2012
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
						BiomeV6Type biome = calcBiomeAtPoint(biomes, world_x, world_z);
					
						float mudAmount = Noise.NoisePerlin2D(noise_mud.np,
								x + 0.5f * noise_mud.np.spread.x, z + 0.5f * noise_mud.np.spread.y, seed);
						int mud_add_amount = (int)(mudAmount / 2.0f + 0.5f);

						int surface_y = (int)baseTerrainLevelFromNoise(world_x, world_z) + mud_add_amount;
						boolean ocean = surface_y <= params.water_level;
						if (ocean) biomeValue |= BITPLANE_OCEAN;
						if (lastTerrainLevelWasMountains) biomeValue |= BITPLANE_MOUNTAIN;
						
						if (!ocean && biome != BiomeV6Type.BT_DESERT && surface_y <= v6params.water_level + 2 && haveBeach(world_x, world_z)) {
							biome = BiomeV6Type.BT_BEACH;
						}
						biomeValue |= biome.ordinal();
						
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
