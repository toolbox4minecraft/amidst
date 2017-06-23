package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class MetadataJson {
	private volatile String model;

	public MetadataJson() {
	}

	public String getModel() {
		return model;
	}
}
