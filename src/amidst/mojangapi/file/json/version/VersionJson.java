package amidst.mojangapi.file.json.version;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.mojangapi.file.LibraryFinder;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;

public class VersionJson {
	private List<LibraryJson> libraries = Collections.emptyList();

	@GsonConstructor
	public VersionJson() {
	}

	public List<LibraryJson> getLibraries() {
		return libraries;
	}

	public List<URL> getLibraryUrls(DotMinecraftDirectory dotMinecraftDirectory) {
		return LibraryFinder.getLibraryUrls(dotMinecraftDirectory, libraries);
	}
}
