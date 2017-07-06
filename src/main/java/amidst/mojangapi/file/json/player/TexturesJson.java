package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class TexturesJson {
	private volatile SKINJson SKIN;

	public TexturesJson() {
	}

	public SKINJson getSKIN() {
		return SKIN;
	}
}
