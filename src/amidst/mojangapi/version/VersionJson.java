package amidst.mojangapi.version;

import java.util.List;

public class VersionJson {
	private List<LibraryJson> libraries;

	public VersionJson() {
		// no-argument constructor for gson
	}

	public List<LibraryJson> getLibraries() {
		return libraries;
	}
}
