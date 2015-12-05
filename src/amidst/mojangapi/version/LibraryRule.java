package amidst.mojangapi.version;

public class LibraryRule {
	private String action;
	private LibraryRuleOs os = new LibraryRuleOs("any");

	public LibraryRule() {
		// no-argument constructor for gson
	}

	public boolean isApplicable() {
		return os.check();
	}

	public boolean isAllowed() {
		return action.equals("allow");
	}

	public String getAction() {
		return action;
	}

	public LibraryRuleOs getOs() {
		return os;
	}
}
