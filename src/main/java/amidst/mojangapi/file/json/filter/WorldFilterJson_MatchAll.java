package amidst.mojangapi.file.json.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.filter.WorldFilter;
import amidst.mojangapi.world.filter.WorldFilter_MatchAll;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Immutable
public class WorldFilterJson_MatchAll {
	public static Optional<WorldFilterJson_MatchAll> from(String queryString) {
		try {
			return Optional.ofNullable(GSON.fromJson(queryString, WorldFilterJson_MatchAll.class));
		} catch (JsonSyntaxException e) {
			return Optional.empty();
		}
	}

	private static final Gson GSON = new Gson();

	private volatile List<WorldFilterJson_Biome> biomeFilters = Collections.emptyList();
	private volatile List<WorldFilterJson_Structure> structureFilters = Collections.emptyList();

	@GsonConstructor
	public WorldFilterJson_MatchAll() {
	}

	public WorldFilter_MatchAll createWorldFilter(int size) {
		return new WorldFilter_MatchAll(size * Resolution.FRAGMENT.getStep(), createFilterList());
	}

	private List<WorldFilter> createFilterList() {
		List<WorldFilter> filters = new ArrayList<WorldFilter>();
		for (WorldFilterJson_Biome biomeFilterJson : biomeFilters) {
			filters.add(biomeFilterJson.createBiomeFilter());
		}

		for (WorldFilterJson_Structure structureFilterJson : structureFilters) {
			filters.add(structureFilterJson.createStructureFilter());
		}
		return filters;
	}
}
