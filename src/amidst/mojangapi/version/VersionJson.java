package amidst.mojangapi.version;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.internal.LibraryFinder;

public class VersionJson {
	private List<LibraryJson> libraries = Collections.emptyList();

	public VersionJson() {
		// no-argument constructor for gson
	}

	public List<LibraryJson> getLibraries() {
		return libraries;
	}

	public List<URL> getLibraryUrls(DotMinecraftDirectory dotMinecraftDirectory) {
		return LibraryFinder.getLibraryUrls(dotMinecraftDirectory, libraries);
	}
}
