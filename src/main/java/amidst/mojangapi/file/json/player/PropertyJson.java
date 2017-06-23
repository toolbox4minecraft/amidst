package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class PropertyJson {
	private volatile String name;
	private volatile String value;

	public PropertyJson() {
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
