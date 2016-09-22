package amidst.gui.seedsearcher;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.filter.WorldFilter;

@Immutable
public class SeedSearcherConfiguration {
	private final WorldFilter worldFilter;
	private final WorldType worldType;
	private final boolean searchContinuously;

	public SeedSearcherConfiguration(WorldFilter worldFilter, WorldType worldType, boolean searchContinuously) {
		this.worldFilter = worldFilter;
		this.worldType = worldType;
		this.searchContinuously = searchContinuously;
	}

	public WorldFilter getWorldFilter() {
		return worldFilter;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public boolean isSearchContinuously() {
		return searchContinuously;
	}
}
