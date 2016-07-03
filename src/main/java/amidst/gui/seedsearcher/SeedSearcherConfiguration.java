package amidst.gui.seedsearcher;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.filter.WorldFilter_MatchAll;

@Immutable
public class SeedSearcherConfiguration {
	private final WorldFilter_MatchAll worldFilter;
	private final WorldType worldType;
	private final boolean searchContinuously;

	public SeedSearcherConfiguration(WorldFilter_MatchAll worldFilter, WorldType worldType, boolean searchContinuously) {
		this.worldFilter = worldFilter;
		this.worldType = worldType;
		this.searchContinuously = searchContinuously;
	}

	public WorldFilter_MatchAll getWorldFilter() {
		return worldFilter;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public boolean isSearchContinuously() {
		return searchContinuously;
	}
}
