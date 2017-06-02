package amidst.mojangapi.file.json.version;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class LibraryRuleJson {
	private volatile String action;
	private volatile LibraryRuleOsJson os;

	@GsonConstructor
	public LibraryRuleJson() {
	}

	public String getAction() {
		return action;
	}

	public LibraryRuleOsJson getOs() {
		return os;
	}
}
