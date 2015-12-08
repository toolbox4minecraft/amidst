package amidst.minecraft;

// TODO: Rename once we figure out what this actually is!
public class BiomeType {
	public static final BiomeType typeA = new BiomeType(0.1F, 0.2F);
	public static final BiomeType typeB = new BiomeType(-0.5F, 0.0F);
	public static final BiomeType typeC = new BiomeType(-1.0F, 0.1F);
	public static final BiomeType typeD = new BiomeType(-1.8F, 0.1F);
	public static final BiomeType typeE = new BiomeType(0.125F, 0.05F);
	public static final BiomeType typeF = new BiomeType(0.2F, 0.2F);
	public static final BiomeType typeG = new BiomeType(0.45F, 0.3F);
	public static final BiomeType typeH = new BiomeType(1.5F, 0.025F);
	public static final BiomeType typeI = new BiomeType(1.0F, 0.5F);
	public static final BiomeType typeJ = new BiomeType(0.0F, 0.025F);
	public static final BiomeType typeK = new BiomeType(0.1F, 0.8F);
	public static final BiomeType typeL = new BiomeType(0.2F, 0.3F);
	public static final BiomeType typeM = new BiomeType(-0.2F, 0.1F);

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
