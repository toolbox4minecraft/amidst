package amidst.mojangapi.file.json.version;

import amidst.utilities.PlatformUtils;

public class LibraryRuleOsJson {
	private String name;

	public LibraryRuleOsJson() {
		// no-argument constructor for gson
	}

	public LibraryRuleOsJson(String name) {
		this.name = name;
	}

	public boolean matches() {
		return name.equals(PlatformUtils.getOs());
	}
}
