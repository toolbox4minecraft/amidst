package amidst.json;

public class RuleOs {
	private String name;

	public RuleOs() {
	}

	public RuleOs(String name) {
		this.name = name;
	}

	public boolean check() {
		return name.equals("any") || name.equals(getOs());
	}

	private String getOs() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return "windows";
		} else if (os.contains("mac")) {
			return "osx";
		} else {
			return "linux";
		}
	}
}
