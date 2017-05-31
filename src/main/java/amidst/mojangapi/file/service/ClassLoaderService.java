package amidst.mojangapi.file.service;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class ClassLoaderService {
	private final LibraryService libraryService = new LibraryService();

	@NotNull
	public URLClassLoader createClassLoader(
			VersionDirectory versionDirectory,
			DotMinecraftDirectory dotMinecraftDirectory) throws FormatException, IOException {
		VersionJson versionJson = JsonReader.readLocation(versionDirectory.getJson(), VersionJson.class);
		List<URL> libraries = libraryService
				.getLibraryUrls(dotMinecraftDirectory.getLibraries(), versionJson.getLibraries());
		libraries.add(versionDirectory.getJar().toURI().toURL());
		return new URLClassLoader(libraries.toArray(new URL[libraries.size()]));
	}
}
