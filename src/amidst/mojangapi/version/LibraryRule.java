package amidst.mojangapi.version;

public class LibraryRule {
	private static final String ACTION_ALLOW = "allow";
	private String action;
	private LibraryRuleOs os;

	public LibraryRule() {
		// no-argument constructor for gson
	}

	public boolean isApplicable() {
		return os == null || os.matches();
	}

	public boolean isAllowed() {
		return action.equals(ACTION_ALLOW);
	}
}
