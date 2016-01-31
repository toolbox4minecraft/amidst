package amidst.mojangapi.world.testworld.storage;

import java.util.Map;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class BiomeDataJson {
	private static int[] short2int(short[] in) {
		int[] result = new int[in.length];
		for (int i = 0; i < in.length; i++) {
			result[i] = in[i];
		}
		return result;
	}

	public static short[] int2short(int[] in) {
		short[] result = new short[in.length];
		for (int i = 0; i < in.length; i++) {
			result[i] = (short) in[i];
		}
		return result;
	}

	private volatile Map<AreaJson, short[]> biomeData;

	@GsonConstructor
	public BiomeDataJson() {
	}

	public BiomeDataJson(Map<AreaJson, short[]> biomeData) {
		this.biomeData = biomeData;
	}

	public int[] get(int x, int y, int width, int height) {
		AreaJson area = new AreaJson(x, y, width, height);
		short[] result = biomeData.get(area);
		if (result != null) {
			return short2int(result);
		} else {
			throw new IllegalArgumentException(
					"the requested area was not stored");
		}
	}
}
