package amidst.mojangapi.file.json.version;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class LibraryRuleOsJson {
	private volatile String name;
	private volatile String version;

	public LibraryRuleOsJson() {
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
}
