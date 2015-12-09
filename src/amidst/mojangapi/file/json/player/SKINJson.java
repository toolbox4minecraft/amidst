package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;

public class SKINJson {
	private String url;
	private MetadataJson metadata;

	@GsonConstructor
	public SKINJson() {
	}

	public String getUrl() {
		return url;
	}

	public MetadataJson getMetadata() {
		return metadata;
	}

	public boolean isSlimModel() {
		return metadata != null && metadata.getModel().equals("slim");
	}
}
