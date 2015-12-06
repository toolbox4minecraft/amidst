package amidst.mojangapi.version;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import amidst.mojangapi.LibraryFinder;

public class VersionJson {
	private List<LibraryJson> libraries = Collections.emptyList();

	public VersionJson() {
		// no-argument constructor for gson
	}

	public List<LibraryJson> getLibraries() {
		return libraries;
	}

	public List<URL> getLibraryUrls(File librariesFile) {
		return LibraryFinder.getLibraryUrls(librariesFile, libraries);
	}
}
