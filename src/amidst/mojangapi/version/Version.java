package amidst.mojangapi.version;

import java.util.List;

public class Version {
	private List<Library> libraries;

	public Version() {
		// no-argument constructor for gson
	}

	public List<Library> getLibraries() {
		return libraries;
	}
}
