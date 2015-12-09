package amidst.mojangapi.file.json.version;

import amidst.documentation.GsonConstructor;
import amidst.utilities.PlatformUtils;

public class LibraryRuleOsJson {
	private String name;

	@GsonConstructor
	public LibraryRuleOsJson() {
	}

	public LibraryRuleOsJson(String name) {
		this.name = name;
	}

	public boolean matches() {
		return name.equals(PlatformUtils.getOs());
	}
}
