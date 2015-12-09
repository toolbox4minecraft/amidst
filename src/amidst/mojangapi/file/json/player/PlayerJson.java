package amidst.mojangapi.file.json.player;

import java.util.Collections;
import java.util.List;

public class PlayerJson {
	private String id;
	private String name;
	private List<PropertyJson> properties = Collections.emptyList();

	public PlayerJson() {
		// no-argument constructor for gson
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

	public TexturesPropertyJson readTexturesProperty() {
		for (PropertyJson property : properties) {
			TexturesPropertyJson texturesProperty = property
					.tryDecodeTexturesProperty();
			if (texturesProperty != null) {
				return texturesProperty;
			}
		}
		return null;
	}
}
