package amidst.mojangapi.file.json.version;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class LibraryRuleOsJson {
	private volatile String name;
	private volatile String version;

	@GsonConstructor
	public LibraryRuleOsJson() {
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
}
