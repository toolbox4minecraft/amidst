package amidst.version;

import java.io.File;

import amidst.logging.Log;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;
import amidst.mojangapi.versionlist.VersionListJson;

public class MinecraftProfile {
	private final LaucherProfileJson profile;
	private final VersionDirectory version;

	public MinecraftProfile(LaucherProfileJson profile,
			DotMinecraftDirectory dotMinecraftDirectory,
			VersionListJson versionList) {
		this.profile = profile;
		this.version = load(profile, dotMinecraftDirectory, versionList);
	}

	private VersionDirectory load(LaucherProfileJson profile,
			DotMinecraftDirectory dotMinecraftDirectory,
			VersionListJson versionList) {
		VersionDirectory result = profile.createVersionDirectory(
				dotMinecraftDirectory, versionList);
		if (result != null) {
			return result;
		} else {
			Log.w("Unable to load version directory for profile: "
					+ profile.getName());
			return null;
		}
	}

	public String getProfileName() {
		return profile.getName();
	}

	public String getGameDir() {
		return profile.getGameDir();
	}

	public boolean getStatus() {
		return version != null;
	}

	public File getJarFile() {
		return version.getJar();
	}
}
