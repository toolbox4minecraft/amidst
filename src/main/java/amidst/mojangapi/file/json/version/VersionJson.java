package amidst.mojangapi.file.json.version;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class VersionJson {
	private volatile List<LibraryJson> libraries = Collections.emptyList();

	@GsonConstructor
	public VersionJson() {
	}

	public List<LibraryJson> getLibraries() {
		return libraries;
	}
}
