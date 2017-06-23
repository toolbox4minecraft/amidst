package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class SKINJson {
	private volatile String url;
	private volatile MetadataJson metadata;

	public SKINJson() {
	}

	public String getUrl() {
		return url;
	}

	public MetadataJson getMetadata() {
		return metadata;
	}
}
