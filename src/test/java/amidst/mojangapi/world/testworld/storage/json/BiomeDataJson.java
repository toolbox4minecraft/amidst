package amidst.mojangapi.world.testworld.storage.json;

import java.util.SortedMap;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.Dimension;

@Immutable
public class BiomeDataJson {
	private static int[] short2int(short[] in) {
		int[] result = new int[in.length];
		for (int i = 0; i < in.length; i++) {
			result[i] = in[i];
		}
		return result;
	}

	public static short[] int2short(int[] in, int outLen) {
		short[] result = new short[outLen];
		for (int i = 0; i < outLen; i++) {
			result[i] = (short) in[i];
		}
		return result;
	}

	private volatile SortedMap<AreaJson, short[]> biomeData;

	@GsonConstructor
	public BiomeDataJson() {
	}

	public BiomeDataJson(SortedMap<AreaJson, short[]> biomeData) {
		this.biomeData = biomeData;
	}

	public int[] get(Dimension dimension, int x, int y, int width, int height) {
		AreaJson area = new AreaJson(dimension, x, y, width, height);
		short[] result = biomeData.get(area);
		if (result != null) {
			return short2int(result);
		} else {
			throw new IllegalArgumentException("the requested area was not stored");
		}
	}
}
