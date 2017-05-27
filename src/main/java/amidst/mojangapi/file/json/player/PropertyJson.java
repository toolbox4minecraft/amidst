package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class PropertyJson {
	private volatile String name;
	private volatile String value;

	@GsonConstructor
	public PropertyJson() {
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
