package amidst.mojangapi.file.json.player;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.MojangApiParsingException;

@Immutable
public class PropertyJson {
	private volatile String name;
	private volatile String value;

	@GsonConstructor
	public PropertyJson() {
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@NotNull
	public String getDecodedValue() throws MojangApiParsingException {
		if (value == null) {
			throw new MojangApiParsingException("unable to decode property value");
		} else {
			return new String(
					Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8)),
					StandardCharsets.UTF_8);
		}
	}

	public boolean isTexturesProperty() throws MojangApiParsingException {
		if (name == null) {
			throw new MojangApiParsingException("property has no name");
		} else {
			return name.equals("textures");
		}
	}
}
