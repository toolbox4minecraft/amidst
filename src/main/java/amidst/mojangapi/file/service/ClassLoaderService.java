package amidst.mojangapi.file.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.version.VersionJson;

@Immutable
public class ClassLoaderService {
	@NotNull
	public URLClassLoader createClassLoader(
			VersionDirectory versionDirectory,
			DotMinecraftDirectory dotMinecraftDirectory) throws MalformedURLException {
		if (versionDirectory.getJson().isFile()) {
			AmidstLogger.info("Loading libraries.");
			return doCreateClassLoader(
					getJarFileUrl(versionDirectory),
					getAllLibraryUrls(versionDirectory, dotMinecraftDirectory));
		} else {
			AmidstLogger
					.info("Unable to find Minecraft library JSON at: " + versionDirectory.getJson() + ". Skipping.");
			return doCreateClassLoader(getJarFileUrl(versionDirectory));
		}
	}

	@NotNull
	private URLClassLoader doCreateClassLoader(URL jarFileUrl, List<URL> libraries) {
		libraries.add(jarFileUrl);
		return new URLClassLoader(libraries.toArray(new URL[libraries.size()]));
	}

	@NotNull
	private URLClassLoader doCreateClassLoader(URL jarFileUrl) {
		return new URLClassLoader(new URL[] { jarFileUrl });
	}

	@NotNull
	private URL getJarFileUrl(VersionDirectory versionDirectory) throws MalformedURLException {
		return versionDirectory.getJar().toURI().toURL();
	}

	private List<URL> getAllLibraryUrls(
			VersionDirectory versionDirectory,
			DotMinecraftDirectory dotMinecraftDirectory) {
		File json = versionDirectory.getJson();
		try {
			return new LibraryService().getLibraryUrls(
					dotMinecraftDirectory.getLibraries(),
					JsonReader.readLocation(json, VersionJson.class).getLibraries());
		} catch (IOException | MojangApiParsingException e) {
			AmidstLogger.warn("Invalid jar profile loaded. Library loading will be skipped. (Path: " + json + ")");
			return new ArrayList<>();
		}
	}
}
