package amidst.mojangapi.file.directory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;

import amidst.logging.Log;
import amidst.mojangapi.file.JsonReader;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.utilities.JavaUtils;

public class VersionDirectory {
	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final String versionId;
	private final File jar;
	private final File json;

	public VersionDirectory(DotMinecraftDirectory dotMinecraftDirectory,
			String versionId, File jar, File json) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
		this.versionId = versionId;
		this.jar = jar;
		this.json = json;
	}

	public boolean isValid() {
		return jar.isFile() && json.isFile();
	}

	public String getVersionId() {
		return versionId;
	}

	public File getJar() {
		return jar;
	}

	public File getJson() {
		return json;
	}

	public VersionJson readVersionJson() throws FileNotFoundException {
		return JsonReader.readVersionFrom(json);
	}

	public List<URL> getAllLibraryUrls() {
		try {
			return readVersionJson().getLibraryUrls(dotMinecraftDirectory);
		} catch (IOException e) {
			Log.w("Invalid jar profile loaded. Library loading will be skipped. (Path: "
					+ json + ")");
		}

		return Collections.emptyList();
	}

	public URLClassLoader createClassLoader() throws MalformedURLException {
		if (json.isFile()) {
			Log.i("Loading libraries.");
			return doCreateClassLoader(getAllLibraryUrls());
		} else {
			Log.i("Unable to find Minecraft library JSON at: " + json
					+ ". Skipping.");
			return doCreateClassLoader();
		}
	}

	private URLClassLoader doCreateClassLoader(List<URL> libraries)
			throws MalformedURLException {
		libraries.add(getJarFileUrl());
		return new URLClassLoader(JavaUtils.toArray(libraries, URL.class));
	}

	private URLClassLoader doCreateClassLoader() throws MalformedURLException {
		return new URLClassLoader(new URL[] { getJarFileUrl() });
	}

	private URL getJarFileUrl() throws MalformedURLException {
		return jar.toURI().toURL();
	}
}
