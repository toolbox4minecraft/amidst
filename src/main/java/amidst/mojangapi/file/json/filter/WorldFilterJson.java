package amidst.mojangapi.file.json.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.BaseFilter;
import amidst.mojangapi.world.filter.BiomeFilter;
import amidst.mojangapi.world.filter.WorldFilter;
import amidst.mojangapi.world.filter.WorldFinder;

@Immutable
public class WorldFilterJson {
	private volatile boolean continuousSearch;
	private volatile String searchName = "Unnamed Search";
	private volatile List<BiomeFilterJson> biomeFilters = Collections.emptyList();
	private volatile List<StructureFilterJson> structureFilters = Collections.emptyList();

	@GsonConstructor
	public WorldFilterJson() {
	}

	public void configureWorldFinder(WorldFinder worldFinder) {
		// Determine max size for buffering in WorldFilter
		long largestBiomeFilterSize = 0;

		List<BaseFilter> filters = new ArrayList<BaseFilter>();
		for (BiomeFilterJson biomeFilterJson : biomeFilters) {
			BiomeFilter filter = biomeFilterJson.createBiomeFilter();
			if (filter.worldFilterSize > largestBiomeFilterSize) {
				largestBiomeFilterSize = filter.worldFilterSize;
			}
			filters.add(filter);
		}

		for (StructureFilterJson structureFilterJson : structureFilters) {
			filters.add(structureFilterJson.createStructureFilter());
		}

		//correct the world filter size to reflect distance from center not size
		WorldFilter worldFilter = new WorldFilter(largestBiomeFilterSize / 2, filters, searchName);
		worldFinder.setWorldFilter(worldFilter);
		worldFinder.setContinuous(continuousSearch);
	}
}