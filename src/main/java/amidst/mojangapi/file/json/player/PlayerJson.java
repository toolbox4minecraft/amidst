package amidst.mojangapi.file.json.player;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class PlayerJson {
	private volatile String id;
	private volatile String name;
	private volatile List<PropertyJson> properties = Collections.emptyList();

	public PlayerJson() {
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<PropertyJson> getProperties() {
		return properties;
	}
}
