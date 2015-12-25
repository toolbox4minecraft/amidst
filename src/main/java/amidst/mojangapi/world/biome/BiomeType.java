package amidst.mojangapi.world.biome;

import amidst.documentation.Immutable;

// TODO: Rename once we figure out what this actually is!
@Immutable
public class BiomeType {
	// @formatter:off
	public static final BiomeType TYPE_A = new BiomeType( 0.1F,   0.2F);
	public static final BiomeType TYPE_B = new BiomeType(-0.5F,   0.0F);
	public static final BiomeType TYPE_C = new BiomeType(-1.0F,   0.1F);
	public static final BiomeType TYPE_D = new BiomeType(-1.8F,   0.1F);
	public static final BiomeType TYPE_E = new BiomeType( 0.125F, 0.05F);
	public static final BiomeType TYPE_F = new BiomeType( 0.2F,   0.2F);
	public static final BiomeType TYPE_G = new BiomeType( 0.45F,  0.3F);
	public static final BiomeType TYPE_H = new BiomeType( 1.5F,   0.025F);
	public static final BiomeType TYPE_I = new BiomeType( 1.0F,   0.5F);
	public static final BiomeType TYPE_J = new BiomeType( 0.0F,   0.025F);
	public static final BiomeType TYPE_K = new BiomeType( 0.1F,   0.8F);
	public static final BiomeType TYPE_L = new BiomeType( 0.2F,   0.3F);
	public static final BiomeType TYPE_M = new BiomeType(-0.2F,   0.1F);
	// @formatter:on

	private final float value1;
	private final float value2;

	public BiomeType(float value1, float value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	public BiomeType getExtreme() {
		return new BiomeType(value1 * 0.8F, value2 * 0.6F);
	}

	public BiomeType getRare() {
		return new BiomeType(value1 + 0.1F, value2 + 0.2F);
	}

	public float getValue1() {
		return value1;
	}

	public float getValue2() {
		return value2;
	}
}
