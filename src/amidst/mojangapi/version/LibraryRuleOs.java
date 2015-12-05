package amidst.mojangapi.version;

import amidst.utilities.PlatformUtils;

public class LibraryRuleOs {
	private String name;

	public LibraryRuleOs() {
		// no-argument constructor for gson
	}

	public LibraryRuleOs(String name) {
		this.name = name;
	}

	public boolean check() {
		return name.equals("any") || name.equals(PlatformUtils.getOs());
	}

	public String getName() {
		return name;
	}
}
