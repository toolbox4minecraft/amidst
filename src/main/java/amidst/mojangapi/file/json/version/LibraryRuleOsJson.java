package amidst.mojangapi.file.json.version;

import java.util.regex.Pattern;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class LibraryRuleOsJson {
	private volatile String name;
	private volatile String version;

	@GsonConstructor
	public LibraryRuleOsJson() {
	}

	public boolean isApplicable(String os, String version) {
		return this.name.equals(os) && (this.version == null || Pattern.matches(this.version, version));
	}
}
