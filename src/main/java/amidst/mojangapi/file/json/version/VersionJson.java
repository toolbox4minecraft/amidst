package amidst.mojangapi.file.json.version;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.LibraryFinder;

@Immutable
public class VersionJson {
	private volatile List<LibraryJson> libraries = Collections.emptyList();

	@GsonConstructor
	public VersionJson() {
	}

	public List<LibraryJson> getLibraries() {
		return libraries;
	}

	@NotNull
	public List<URL> getLibraryUrls(File librariesDirectory) {
		return LibraryFinder.getLibraryUrls(librariesDirectory, libraries);
	}
}
