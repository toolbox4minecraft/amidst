package amidst.mojangapi;

import java.io.File;

import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.versionlist.VersionListJson;

public enum MojangApi {
	;

	public static VersionListJson readRemoteOrLocalVersionList() {
		return JsonReader.readRemoteOrLocalVersionList();
	}

	public static DotMinecraftDirectory createDotMinecraftDirectory(
			String preferedDotMinecraftDirectory,
			String preferedMinecraftLibraries) {
		if (preferedMinecraftLibraries != null) {
			return new DotMinecraftDirectory(
					DotMinecraftDirectoryFinder
							.find(preferedDotMinecraftDirectory),
					new File(preferedMinecraftLibraries));
		} else {
			return new DotMinecraftDirectory(
					DotMinecraftDirectoryFinder
							.find(preferedDotMinecraftDirectory));
		}
	}
}
