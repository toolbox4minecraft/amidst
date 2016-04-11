package amidst.mojangapi.file.json.player;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.JsonReader;

@Immutable
public class PlayerJson {
	private volatile String id;
	private volatile String name;
	private volatile List<PropertyJson> properties = Collections.emptyList();

	@GsonConstructor
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

	@NotNull
	public TexturesPropertyJson readTexturesProperty() throws MojangApiParsingException {
		for (PropertyJson property : properties) {
			if (property.isTexturesProperty()) {
				return JsonReader.read(property.getDecodedValue(), TexturesPropertyJson.class);
			}
		}
		throw new MojangApiParsingException("player json does not contain the textures property");
	}

	@NotNull
	public String getSkinUrl() throws MojangApiParsingException {
		try {
			String result = readTexturesProperty().getTextures().getSKIN().getUrl();
			if (result != null) {
				return result;
			} else {
				throw new MojangApiParsingException("unable to get skin url");
			}
		} catch (NullPointerException e) {
			throw new MojangApiParsingException("unable to get skin url", e);
		}
	}
}
