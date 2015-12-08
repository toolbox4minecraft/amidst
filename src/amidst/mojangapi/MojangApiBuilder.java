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
		result.set(createProfileDirectory(), createVersionDirectory(result));
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

	private VersionDirectory createVersionDirectory(MojangApi mojangApi) {
		if (preferedVersionJar != null) {
			File jar = new File(preferedVersionJar);
			File json = new File(getJsonFileName());
			VersionDirectory result = mojangApi.createVersionDirectory(jar,
					json);
			if (result.isValid()) {
				return result;
			}
		}
		return null;
	}

	private String getJsonFileName() {
		return preferedVersionJar.substring(0, preferedVersionJar.length() - 4)
				+ ".json";
	}
}
