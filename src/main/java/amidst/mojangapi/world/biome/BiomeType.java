package amidst.mojangapi.world.biome;

import amidst.documentation.Immutable;

// TODO: Rename once we figure out what this actually is!
@Immutable
public class BiomeType {
	// @formatter:off
	public static final BiomeType DEFAULT_TERRAIN_DATA         = new BiomeType( 0.1F,   0.2F);
	public static final BiomeType RIVER_TERRAIN_DATA           = new BiomeType(-0.5F,   0.0F);
	public static final BiomeType OCEAN_TERRAIN_DATA           = new BiomeType(-1.0F,   0.1F);
	public static final BiomeType DEEP_OCEAN_TERRAIN_DATA      = new BiomeType(-1.8F,   0.1F);
	public static final BiomeType PLAINS_TERRAIN_DATA          = new BiomeType( 0.125F, 0.05F);
	public static final BiomeType TAIGA_TERRAIN_DATA           = new BiomeType( 0.2F,   0.2F);
	public static final BiomeType HILLS_TERRAIN_DATA           = new BiomeType( 0.45F,  0.3F);
	public static final BiomeType PLATEAU_TERRAIN_DATA         = new BiomeType( 1.5F,   0.025F);
	public static final BiomeType EXTREME_HILLS_TERRAIN_DATA   = new BiomeType( 1.0F,   0.5F);
	public static final BiomeType BEACH_TERRAIN_DATA           = new BiomeType( 0.0F,   0.025F);
	public static final BiomeType STONE_BEACH_TERRAIN_DATA     = new BiomeType( 0.1F,   0.8F);
	public static final BiomeType MUSHROOM_ISLAND_TERRAIN_DATA = new BiomeType( 0.2F,   0.3F);
	public static final BiomeType SWAMPLAND_TERRAIN_DATA       = new BiomeType(-0.2F,   0.1F);
	// @formatter:on

	private final float height;
	private final float heightVolatility;

	public BiomeType(float height, float heightVolatility) {
		this.height = height;
		this.heightVolatility = heightVolatility;
	}

	public BiomeType getExtreme() {
		return new BiomeType(height * 0.8F, heightVolatility * 0.6F);
	}

	public BiomeType getRare() {
		return new BiomeType(height + 0.1F, heightVolatility + 0.2F);
	}

	public float getHeight() {
		return height;
	}

	public float getHeightVolatility() {
		return heightVolatility;
	}
}
