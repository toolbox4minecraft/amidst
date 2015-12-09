package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;

public class TexturesJson {
	private SKINJson SKIN;

	@GsonConstructor
	public TexturesJson() {
	}

	public SKINJson getSKIN() {
		return SKIN;
	}
}
