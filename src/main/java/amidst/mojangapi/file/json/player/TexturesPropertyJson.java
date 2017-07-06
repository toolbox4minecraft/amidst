package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class TexturesPropertyJson {
	private volatile TexturesJson textures;

	public TexturesPropertyJson() {
	}

	public TexturesJson getTextures() {
		return textures;
	}
}
