package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class SKINJson {
	private volatile String url;
	private volatile MetadataJson metadata;

	@GsonConstructor
	public SKINJson() {
	}

	public String getUrl() {
		return url;
	}

	public MetadataJson getMetadata() {
		return metadata;
	}
}
