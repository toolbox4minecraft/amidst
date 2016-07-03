package amidst.mojangapi.file.json.filter;

import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.WorldFilter_Structure;

@Immutable
public class WorldFilterJson_Structure {
	private volatile long distance;
	private volatile String structure;
	private volatile int minimum;

	@GsonConstructor
	public WorldFilterJson_Structure() {
	}

	public void validate(List<String> notifications) {
		// noop
	}

	public WorldFilter_Structure createStructureFilter() {
		return new WorldFilter_Structure(distance, structure, minimum);
	}
}
