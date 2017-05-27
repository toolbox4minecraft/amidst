package amidst.mojangapi.file;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.player.PlayerJson;
import amidst.mojangapi.file.json.player.PropertyJson;
import amidst.mojangapi.file.json.player.SKINJson;
import amidst.mojangapi.file.json.player.TexturesJson;
import amidst.mojangapi.file.json.player.TexturesPropertyJson;

@Immutable
public class PlayerSkinService {
	@NotNull
	public String getSkinUrl(PlayerJson playerJson) throws MojangApiParsingException {
		return readTexturesProperty(playerJson)
				.map(TexturesPropertyJson::getTextures)
				.map(TexturesJson::getSKIN)
				.map(SKINJson::getUrl)
				.orElseThrow(() -> new MojangApiParsingException("unable to get skin url"));
	}

	@NotNull
	private Optional<TexturesPropertyJson> readTexturesProperty(PlayerJson playerJson)
			throws MojangApiParsingException {
		for (PropertyJson property : playerJson.getProperties()) {
			if (isTexturesProperty(property)) {
				return Optional.of(JsonReader.read(getDecodedValue(property), TexturesPropertyJson.class));
			}
		}
		return Optional.empty();
	}

	private boolean isTexturesProperty(PropertyJson propertyJson) throws MojangApiParsingException {
		String name = propertyJson.getName();
		if (name == null) {
			throw new MojangApiParsingException("property has no name");
		} else {
			return name.equals("textures");
		}
	}

	@NotNull
	private String getDecodedValue(PropertyJson propertyJson) throws MojangApiParsingException {
		String value = propertyJson.getValue();
		if (value == null) {
			throw new MojangApiParsingException("unable to decode property value");
		} else {
			return new String(
					Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8)),
					StandardCharsets.UTF_8);
		}
	}
}
