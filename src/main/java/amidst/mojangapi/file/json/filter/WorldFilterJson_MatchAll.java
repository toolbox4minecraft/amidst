package amidst.mojangapi.file.json.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.filter.WorldFilter;
import amidst.mojangapi.world.filter.WorldFilter_MatchAll;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class WorldFilterJson_MatchAll {
	public static Optional<WorldFilterJson_MatchAll> from(String queryString) {
		try {
			return Optional.of(JsonReader.readString(queryString, WorldFilterJson_MatchAll.class));
		} catch (FormatException e) {
			return Optional.empty();
		}
	}

	private volatile List<WorldFilterJson_Biome> biomeFilters = Collections.emptyList();
	private volatile List<WorldFilterJson_Structure> structureFilters = Collections.emptyList();

	@GsonConstructor
	public WorldFilterJson_MatchAll() {
	}

	public List<String> getValidationMessages() {
		List<String> result = new LinkedList<>();
		validate(result);
		return result;
	}

	private void validate(List<String> notifications) {
		biomeFilters.forEach(f -> f.validate(notifications));
		structureFilters.forEach(f -> f.validate(notifications));
	}

	public WorldFilter createWorldFilter() {
		// TODO: the size is 0, because this filter will never use its biome
		// data
		return new WorldFilter_MatchAll(0, createFilterList());
	}

	public Optional<WorldFilter> createValidWorldFilter() {
		if (getValidationMessages().isEmpty()) {
			return Optional.of(createWorldFilter());
		} else {
			// TODO: use error messages
			AmidstLogger.debug(getValidationMessages().toString());
			return Optional.empty();
		}
	}

	private List<WorldFilter> createFilterList() {
		List<WorldFilter> filters = new ArrayList<>();
		for (WorldFilterJson_Biome biomeFilterJson : biomeFilters) {
			filters.add(biomeFilterJson.createBiomeFilter());
		}

		for (WorldFilterJson_Structure structureFilterJson : structureFilters) {
			filters.add(structureFilterJson.createStructureFilter());
		}
		return filters;
	}
}
