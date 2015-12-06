package amidst.version;

import java.io.File;
import java.util.Map;

import amidst.logging.Log;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.mojangapi.dotminecraft.VersionDirectory;

public class MinecraftVersion {
	private final VersionDirectory versionDirectory;

	private MinecraftVersion(VersionDirectory versionDirectory) {
		this.versionDirectory = versionDirectory;
	}

	public static MinecraftVersion fromVersionId(String lastVersionId) {
		return fromVersionPath(lastVersionId);
	}

	private static MinecraftVersion fromVersionPath(String versionId) {
		VersionDirectory versionDirectory = LocalMinecraftInstallation
				.getDotMinecraftDirectory().createVersionDirectory(versionId);
		if (versionDirectory.isValid()) {
			return new MinecraftVersion(versionDirectory);
		} else {
			Log.w("Unable to load version directory: "
					+ versionDirectory.getJar());
			return null;
		}
	}

	public static MinecraftVersion fromLatestRelease(
			Map<String, String>[] versions) {
		MinecraftVersion version = null;
		for (int i = 0; i < versions.length; i++) {
			if (versions[i].get("type").equals("release")
					&& (version = fromVersionId(versions[i].get("id"))) != null) {
				return version;
			}
		}
		return null;
	}

	public static MinecraftVersion fromLatestSnapshot(
			Map<String, String>[] versions) {
		MinecraftVersion version = null;
		for (int i = 0; i < versions.length; i++) {
			if ((version = fromVersionId(versions[i].get("id"))) != null) {
				return version;
			}
		}
		return null;
	}

	public String getName() {
		return versionDirectory.getVersionId();
	}

	public File getJarFile() {
		return versionDirectory.getJar();
	}
}
