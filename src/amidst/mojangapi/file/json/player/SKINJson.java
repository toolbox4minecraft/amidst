package amidst.mojangapi.file.json.player;

public class SKINJson {
	private String url;
	private MetadataJson metadata;

	public SKINJson() {
		// no-argument constructor for gson
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
