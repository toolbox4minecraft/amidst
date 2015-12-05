package amidst.mojangapi.version;

import amidst.utilities.PlatformUtils;

public class RuleOs {
	private String name;

	public RuleOs() {
		// no-argument constructor for gson
	}

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
