package amidst.mojangapi.world.testworld.storage.json;

import java.util.SortedMap;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.Region;

@Immutable
@GsonObject
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

	private volatile SortedMap<AreaJson, short[]> biomeData;

	public BiomeDataJson() {
	}

	public BiomeDataJson(SortedMap<AreaJson, short[]> biomeData) {
		this.biomeData = biomeData;
	}

	public int[] get(Region.Box region) {
		short[] result = biomeData.get(AreaJson.from(region));
		if (result != null) {
			return short2int(result);
		} else {
			throw new IllegalArgumentException("the requested area was not stored");
		}
	}
}
