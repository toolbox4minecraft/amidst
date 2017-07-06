package amidst.mojangapi.file.json.version;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class LibraryRuleJson {
	private volatile String action;
	private volatile LibraryRuleOsJson os;

	public LibraryRuleJson() {
	}

	public String getAction() {
		return action;
	}

	public LibraryRuleOsJson getOs() {
		return os;
	}
}
