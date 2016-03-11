package amidst.mojangapi.file.json.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.BaseFilter;
import amidst.mojangapi.world.filter.WorldFilter;
import amidst.mojangapi.world.filter.WorldFinder;

@Immutable
public class WorldFilterJson {
	private volatile boolean continuousSearch;
	private volatile List<BiomeFilterJson> biomeFilters = Collections.emptyList();
	private volatile List<StructureFilterJson> structureFilters = Collections.emptyList();

	@GsonConstructor
	public WorldFilterJson() {
	}

	public void configureWorldFinder(WorldFinder worldFinder) {
		List<BaseFilter> filters = new ArrayList<BaseFilter>();
		for (BiomeFilterJson biomeFilterJson : biomeFilters) {
			filters.add(biomeFilterJson.createBiomeFilter());
		}

		for (StructureFilterJson structureFilterJson : structureFilters) {
			filters.add(structureFilterJson.createStructureFilter());
		}

		WorldFilter worldFilter = new WorldFilter(0, filters);
		worldFinder.setWorldFilter(worldFilter);
		worldFinder.setContinuous(continuousSearch);
	}
}