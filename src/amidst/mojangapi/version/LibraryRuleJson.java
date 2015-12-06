package amidst.mojangapi.version;

public class LibraryRuleJson {
	private static final String ACTION_ALLOW = "allow";

	private String action;
	private LibraryRuleOsJson os;

	public LibraryRuleJson() {
		// no-argument constructor for gson
	}

	public boolean isApplicable() {
		return os == null || os.matches();
	}

	public boolean isAllowed() {
		return action.equals(ACTION_ALLOW);
	}
}
