package amidst.mojangapi.world.biome;

import amidst.documentation.Immutable;

@Immutable
public class BiomeType {	
	// @formatter:off
	public static final BiomeType PLAINS            = new BiomeType( 0.1F,   0.2F);
	public static final BiomeType RIVER             = new BiomeType(-0.5F,   0.0F);
	public static final BiomeType OCEAN             = new BiomeType(-1.0F,   0.1F);
	public static final BiomeType DEEP_OCEAN        = new BiomeType(-1.8F,   0.1F);
	public static final BiomeType PLAINS_FLAT       = new BiomeType( 0.125F, 0.05F);
	public static final BiomeType PLAINS_TAIGA      = new BiomeType( 0.2F,   0.2F);
	public static final BiomeType HILLS             = new BiomeType( 0.45F,  0.3F);
	public static final BiomeType PLATEAU           = new BiomeType( 1.5F,   0.025F);
	public static final BiomeType MOUNTAINS         = new BiomeType( 1.0F,   0.5F);
	public static final BiomeType BEACH             = new BiomeType( 0.0F,   0.025F);
	public static final BiomeType BEACH_CLIFFS      = new BiomeType( 0.1F,   0.8F);
	public static final BiomeType ISLAND            = new BiomeType( 0.2F,   0.3F);
	public static final BiomeType SWAMPLAND         = new BiomeType(-0.2F,   0.1F);
	// @formatter:on

	private final float biomeDepth;
	private final float biomeFactor;

	public BiomeType(float biomeDepth, float biomeFactor) {
		this.biomeDepth = biomeDepth;
		this.biomeFactor = biomeFactor;
	}

	public BiomeType weaken() {
		return new BiomeType(biomeDepth * 0.8F, biomeFactor * 0.6F);
	}

	public BiomeType strengthen() {
		return new BiomeType(biomeDepth + 0.1F, biomeFactor + 0.2F);
	}

	public float getBiomeDepth() {
		return biomeDepth;
	}

	public float getBiomeFactor() {
		return biomeFactor;
	}
}
