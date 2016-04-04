package amidst.mojangapi.file.directory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.Log;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceBuilder;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;

@Immutable
public class VersionDirectory {
	private static final LocalMinecraftInterfaceBuilder LOCAL_MINECRAFT_INTERFACE_BUILDER = new LocalMinecraftInterfaceBuilder();

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

	@NotNull
	public VersionJson readVersionJson() throws MojangApiParsingException,
			IOException {
		return JsonReader.readVersionFrom(json);
	}

	@NotNull
	public URLClassLoader createClassLoader() throws MalformedURLException {
		if (json.isFile()) {
			Log.i("Loading libraries.");
			return doCreateClassLoader(getJarFileUrl(), getAllLibraryUrls());
		} else {
			Log.i("Unable to find Minecraft library JSON at: " + json
					+ ". Skipping.");
			return doCreateClassLoader(getJarFileUrl());
		}
	}

	@NotNull
	private URL getJarFileUrl() throws MalformedURLException {
		return jar.toURI().toURL();
	}

	@NotNull
	private List<URL> getAllLibraryUrls() {
		try {
			return readVersionJson().getLibraryUrls(
					dotMinecraftDirectory.getLibraries());
		} catch (IOException | MojangApiParsingException e) {
			Log.w("Invalid jar profile loaded. Library loading will be skipped. (Path: "
					+ json + ")");
			return new ArrayList<URL>();
		}
	}

	@NotNull
	private URLClassLoader doCreateClassLoader(URL jarFileUrl,
			List<URL> libraries) {
		libraries.add(jarFileUrl);
		return new URLClassLoader(libraries.toArray(new URL[libraries.size()]));
	}

	@NotNull
	private URLClassLoader doCreateClassLoader(URL jarFileUrl) {
		return new URLClassLoader(new URL[] { jarFileUrl });
	}

	@NotNull
	public MinecraftInterface createLocalMinecraftInterface()
			throws LocalMinecraftInterfaceCreationException {
		return LOCAL_MINECRAFT_INTERFACE_BUILDER.create(this);
	}
}
