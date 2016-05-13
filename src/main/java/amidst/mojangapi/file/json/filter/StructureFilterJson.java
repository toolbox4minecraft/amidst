package amidst.mojangapi.file.json.filter;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.StructureFilter;

@Immutable
public class StructureFilterJson {
	private volatile long distance;
	private volatile String structure;
	private volatile int minimum;
	private volatile String group = "Default";
	private volatile long scoreValue = 0;

	@GsonConstructor
	public StructureFilterJson() {
	}

	public StructureFilter createStructureFilter() {
		return new StructureFilter(distance, structure, minimum, group, scoreValue);
	}
}