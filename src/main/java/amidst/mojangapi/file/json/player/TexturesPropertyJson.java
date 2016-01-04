package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class TexturesPropertyJson {
	private volatile TexturesJson textures;

	@GsonConstructor
	public TexturesPropertyJson() {
	}

	public TexturesJson getTextures() {
		return textures;
	}
}
