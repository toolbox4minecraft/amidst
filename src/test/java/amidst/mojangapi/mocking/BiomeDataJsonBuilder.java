package amidst.mojangapi.mocking;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.testworld.storage.json.AreaJson;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;

@NotThreadSafe
public class BiomeDataJsonBuilder {
	private final SortedMap<AreaJson, short[]> quarterBiomeData = new TreeMap<>();
	private final SortedMap<AreaJson, short[]> fullBiomeData = new TreeMap<>();

	public void store(Region.Box region, boolean useQuarterResolution, int[] biomeData) {
		store(getBiomeDataMap(useQuarterResolution),region, biomeData);
	}

	private Map<AreaJson, short[]> getBiomeDataMap(boolean useQuarterResolution) {
		if (useQuarterResolution) {
			return quarterBiomeData;
		} else {
			return fullBiomeData;
		}
	}

	private void store(Map<AreaJson, short[]> biomeDataMap, Region.Box region, int[] biomeData) {
		biomeDataMap.put(AreaJson.from(region), BiomeDataJson.int2short(biomeData));
	}

	public BiomeDataJson createQuarterBiomeData() {
		return new BiomeDataJson(quarterBiomeData);
	}

	public BiomeDataJson createFullBiomeData() {
		return new BiomeDataJson(fullBiomeData);
	}
}
