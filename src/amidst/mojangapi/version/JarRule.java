package amidst.mojangapi.version;

public class JarRule {
	private String action;
	private RuleOs os = new RuleOs("any");

	public JarRule() {
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

	public RuleOs getOs() {
		return os;
	}
}
