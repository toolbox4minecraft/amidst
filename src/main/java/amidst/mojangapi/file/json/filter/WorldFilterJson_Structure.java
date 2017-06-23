package amidst.mojangapi.file.json.filter;

import java.util.List;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.WorldFilter_Structure;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

@Immutable
@GsonObject
public class WorldFilterJson_Structure {
	@SuppressWarnings("unused")
	private volatile long distance;
	private volatile String structure;
	private volatile int minimum;

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
		return null;
	}
}
