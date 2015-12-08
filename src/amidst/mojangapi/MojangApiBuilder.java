package amidst.mojangapi;

import java.io.File;

import amidst.logging.Log;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.internal.DotMinecraftDirectoryFinder;
import amidst.mojangapi.internal.JsonReader;
import amidst.mojangapi.versionlist.VersionListJson;

public class MojangApiBuilder {
	public static final String UNKNOWN_VERSION_ID = "unknown";

	private final String preferedDotMinecraftDirectory;
	private final String preferedLibraries;
	private final String preferedVersionJar;
	private final String preferedVersionJson;

	public MojangApiBuilder(String preferedDotMinecraftDirectory,
			String preferedLibraries, String preferedVersionJar,
			String preferedVersionJson) {
		this.preferedDotMinecraftDirectory = preferedDotMinecraftDirectory;
		this.preferedLibraries = preferedLibraries;
		this.preferedVersionJar = preferedVersionJar;
		this.preferedVersionJson = preferedVersionJson;
	}

	public MojangApi construct() {
		DotMinecraftDirectory dotMinecraftDirectory = createDotMinecraftDirectory();
		if (!dotMinecraftDirectory.isValid()) {
			Log.crash("Unable to find minecraft directory at: "
					+ dotMinecraftDirectory.getRoot());
		}
		MojangApi result = new MojangApi(dotMinecraftDirectory,
				readRemoteOrLocalVersionList(), createPreferedJson());
		result.set(createProfileDirectory(),
				createVersionDirectory(dotMinecraftDirectory));
		return result;
	}

	private DotMinecraftDirectory createDotMinecraftDirectory() {
		if (preferedLibraries != null) {
			return new DotMinecraftDirectory(
					DotMinecraftDirectoryFinder
							.find(preferedDotMinecraftDirectory),
					new File(preferedLibraries));
		} else {
			return new DotMinecraftDirectory(
					DotMinecraftDirectoryFinder
							.find(preferedDotMinecraftDirectory));
		}
	}

	private VersionListJson readRemoteOrLocalVersionList() {
		return JsonReader.readRemoteOrLocalVersionList();
	}

	private File createPreferedJson() {
		if (preferedVersionJson != null) {
			File result = new File(preferedVersionJson);
			if (result.isFile()) {
				return result;
			}
		}
		return null;
	}

	// TODO: check for correctness
	private ProfileDirectory createProfileDirectory() {
		if (preferedDotMinecraftDirectory != null) {
			ProfileDirectory result = new ProfileDirectory(new File(
					preferedDotMinecraftDirectory));
			if (result.isValid()) {
				return result;
			}
		}
		return null;
	}

	// TODO: check for correctness
	private VersionDirectory createVersionDirectory(
			DotMinecraftDirectory dotMinecraftDirectory) {
		if (preferedVersionJar != null) {
			File jar = new File(preferedVersionJar);
			File json = new File(jar.getPath().replace(".jar", ".json"));
			VersionDirectory result = new VersionDirectory(
					dotMinecraftDirectory, UNKNOWN_VERSION_ID, jar, json);
			if (result.isValid()) {
				return result;
			}
		}
		return null;
	}
}
