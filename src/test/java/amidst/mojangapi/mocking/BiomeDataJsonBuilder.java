package amidst.mojangapi.mocking;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.testworld.storage.json.AreaJson;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;

@NotThreadSafe
public class BiomeDataJsonBuilder {
	private final SortedMap<AreaJson, short[]> quarterBiomeData = new TreeMap<AreaJson, short[]>();
	private final SortedMap<AreaJson, short[]> fullBiomeData = new TreeMap<AreaJson, short[]>();

	public void store(int x, int y, int width, int height, boolean useQuarterResolution, int[] biomeData) {
		store(getBiomeDataMap(useQuarterResolution), x, y, width, height, biomeData);
	}

	private Map<AreaJson, short[]> getBiomeDataMap(boolean useQuarterResolution) {
		if (useQuarterResolution) {
			return quarterBiomeData;
		} else {
			return fullBiomeData;
		}
	}

	private void store(Map<AreaJson, short[]> biomeDataMap, int x, int y, int width, int height, int[] biomeData) {
		biomeDataMap.put(new AreaJson(x, y, width, height), BiomeDataJson.int2short(biomeData));
	}

	public BiomeDataJson createQuarterBiomeData() {
		return new BiomeDataJson(quarterBiomeData);
	}

	public BiomeDataJson createFullBiomeData() {
		return new BiomeDataJson(fullBiomeData);
	}
}
