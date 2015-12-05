package amidst.json;

import amidst.utilities.PlatformUtils;

public class RuleOs {
	private String name;

	public RuleOs(String name) {
		this.name = name;
	}

	public boolean check() {
		return name.equals("any") || name.equals(PlatformUtils.getOs());
	}

	public String getName() {
		return name;
	}
}
