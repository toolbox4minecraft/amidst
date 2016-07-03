package amidst.mojangapi.file.json.filter;

import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.WorldFilter_Structure;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

@Immutable
public class WorldFilterJson_Structure {
	private volatile long distance;
	private volatile String structure;
	private volatile int minimum;

	@GsonConstructor
	public WorldFilterJson_Structure() {
	}

	public void validate(List<String> notifications) {
		if (!DefaultWorldIconTypes.exists(structure)) {
			notifications.add("invalid structure: '" + structure + "'");
		}
		if (minimum <= 0) {
			notifications.add("invalid minimum: " + minimum);
		}
	}

	public WorldFilter_Structure createStructureFilter() {
		return new WorldFilter_Structure(distance, DefaultWorldIconTypes.getByName(structure), minimum);
	}
}
