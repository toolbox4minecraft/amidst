package amidst.mojangapi.file.json.version;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class LibraryRuleJson {
	private static final String ACTION_ALLOW = "allow";

	private volatile String action;
	private volatile LibraryRuleOsJson os;

	@GsonConstructor
	public LibraryRuleJson() {
	}

	public boolean isApplicable() {
		return os == null || os.matches();
	}

	public boolean isAllowed() {
		return action.equals(ACTION_ALLOW);
	}
}
