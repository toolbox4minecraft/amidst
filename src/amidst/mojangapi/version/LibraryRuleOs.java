package amidst.mojangapi.version;

import amidst.utilities.PlatformUtils;

public class LibraryRuleOs {
	private static final String RULE_OS_NAME_ANY = "any";

	public static LibraryRuleOs any() {
		return new LibraryRuleOs(RULE_OS_NAME_ANY);
	}

	private String name;

	public LibraryRuleOs() {
		// no-argument constructor for gson
	}

	public LibraryRuleOs(String name) {
		this.name = name;
	}

	public boolean check() {
		return name.equals(RULE_OS_NAME_ANY)
				|| name.equals(PlatformUtils.getOs());
	}
}
