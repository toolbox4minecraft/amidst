package amidst.json;

public class JarRule {
	private String action;
	private RuleOs os = new RuleOs("any");

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
