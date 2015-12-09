package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;

public class MetadataJson {
	private String model;

	@GsonConstructor
	public MetadataJson() {
	}

	public String getModel() {
		return model;
	}
}
