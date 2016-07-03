package amidst.mojangapi.file.json.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.BaseFilter;
import amidst.mojangapi.world.filter.WorldFilter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Immutable
public class WorldFilterJson {
	public static Optional<WorldFilterJson> from(String queryString) {
		try {
			return Optional.ofNullable(GSON.fromJson(queryString, WorldFilterJson.class));
		} catch (JsonSyntaxException e) {
			return Optional.empty();
		}
	}

	private static final Gson GSON = new Gson();

	private volatile List<BiomeFilterJson> biomeFilters = Collections.emptyList();
	private volatile List<StructureFilterJson> structureFilters = Collections.emptyList();

	@GsonConstructor
	public WorldFilterJson() {
	}

	public WorldFilter createWorldFilter() {
		return new WorldFilter(0, createFilterList());
	}

	private List<BaseFilter> createFilterList() {
		List<BaseFilter> filters = new ArrayList<BaseFilter>();
		for (BiomeFilterJson biomeFilterJson : biomeFilters) {
			filters.add(biomeFilterJson.createBiomeFilter());
		}

		for (StructureFilterJson structureFilterJson : structureFilters) {
			filters.add(structureFilterJson.createStructureFilter());
		}
		return filters;
	}
}
