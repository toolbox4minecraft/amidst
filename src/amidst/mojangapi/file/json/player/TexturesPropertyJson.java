package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;

public class TexturesPropertyJson {
	private TexturesJson textures;

	@GsonConstructor
	public TexturesPropertyJson() {
	}

	public TexturesJson getTextures() {
		return textures;
	}
}
