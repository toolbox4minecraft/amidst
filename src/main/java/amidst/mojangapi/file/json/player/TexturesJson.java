package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class TexturesJson {
	private volatile SKINJson SKIN;

	@GsonConstructor
	public TexturesJson() {
	}

	public SKINJson getSKIN() {
		return SKIN;
	}
}
