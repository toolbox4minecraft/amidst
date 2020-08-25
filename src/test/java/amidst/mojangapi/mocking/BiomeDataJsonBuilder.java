package amidst.mojangapi.mocking;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.testworld.storage.json.AreaJson;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;

@NotThreadSafe
public class BiomeDataJsonBuilder {
	private final SortedMap<AreaJson, short[]> quarterBiomeData = new TreeMap<>();
	private final SortedMap<AreaJson, short[]> fullBiomeData = new TreeMap<>();

	public void store(Dimension dimension, int x, int y, int width, int height, boolean useQuarterResolution, int[] biomeData) {
		store(getBiomeDataMap(useQuarterResolution), dimension, x, y, width, height, biomeData);
	}

	private Map<AreaJson, short[]> getBiomeDataMap(boolean useQuarterResolution) {
		if (useQuarterResolution) {
			return quarterBiomeData;
		} else {
			return fullBiomeData;
		}
	}

	private void store(Map<AreaJson, short[]> biomeDataMap, Dimension dimension, int x, int y, int width, int height, int[] biomeData) {
		// Store biome data, and trim the array to exclude potential garbage data
		// at the end, which isn't deterministic
		biomeDataMap.put(new AreaJson(dimension, x, y, width, height), BiomeDataJson.int2short(biomeData, width*height));
	}

	public BiomeDataJson createQuarterBiomeData() {
		return new BiomeDataJson(quarterBiomeData);
	}

	public BiomeDataJson createFullBiomeData() {
		return new BiomeDataJson(fullBiomeData);
	}
}
