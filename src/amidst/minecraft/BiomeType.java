package amidst.minecraft;

// TODO: Rename once we figure out what this actually is!
public class BiomeType {
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
