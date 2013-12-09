package amidst.json;

public class JarRule {
	public String action;
	public RuleOs os = new RuleOs("any");
	
	public JarRule() {
	}
	public boolean isApplicable() {
		return os.check();
	}
	public boolean isAllowed() {
		return action.equals("allow");
	}
}
