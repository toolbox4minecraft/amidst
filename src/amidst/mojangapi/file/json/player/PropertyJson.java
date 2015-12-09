package amidst.mojangapi.file.json.player;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import amidst.mojangapi.file.json.JsonReader;

public class PropertyJson {
	private String name;
	private String value;

	public PropertyJson() {
		// no-argument constructor for gson
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	// TODO: this uses java8 classes ... find another base64 decoder?
	public String getDecodedValue() {
		return new String(Base64.getDecoder().decode(
				value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}

	public TexturesPropertyJson tryDecodeTexturesProperty() {
		if (name.equals("textures")) {
			return JsonReader.read(getDecodedValue(),
					TexturesPropertyJson.class);
		} else {
			return null;
		}
	}
}
