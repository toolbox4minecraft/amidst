package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class MetadataJson {
	private volatile String model;

	@GsonConstructor
	public MetadataJson() {
	}

	public String getModel() {
		return model;
	}
}
