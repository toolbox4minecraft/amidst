package amidst.version;

import java.io.File;
import java.util.Map;

import amidst.logging.Log;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.mojangapi.ReleaseType;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;
import amidst.version.LatestVersionList.VersionList;

public class MinecraftProfile {
	public enum Status {
		MISSING("missing"), FAILED("failed"), FOUND("found");

		private final String name;

		private Status(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private VersionDirectory version;
	private final LaucherProfileJson profile;

	private Status status;

	public MinecraftProfile(LaucherProfileJson profile, VersionList versionList) {
		this.profile = profile;
		this.status = load(profile, versionList);
	}

	private Status load(LaucherProfileJson profile, VersionList versionList) {
		if (profile.hasLastVersionId()) {
			version = fromVersionId(profile.getLastVersionId());
			if (version != null) {
				return Status.FOUND;
			} else {
				return Status.MISSING;
			}
		} else if (versionList != null) {
			if (profile.isAllowed(ReleaseType.SNAPSHOT)) {
				version = fromLatestSnapshot(versionList.versions);
			} else {
				version = fromLatestRelease(versionList.versions);
			}
			if (version != null) {
				return Status.FOUND;
			} else {
				return Status.FAILED;
			}
		} else {
			return Status.FAILED;
		}
	}

	public static VersionDirectory fromVersionId(String lastVersionId) {
		return fromVersionPath(lastVersionId);
	}

	private static VersionDirectory fromVersionPath(String versionId) {
		VersionDirectory versionDirectory = LocalMinecraftInstallation
				.getDotMinecraftDirectory().createVersionDirectory(versionId);
		if (versionDirectory.isValid()) {
			return versionDirectory;
		} else {
			Log.w("Unable to load version directory: "
					+ versionDirectory.getJar());
			return null;
		}
	}

	public static VersionDirectory fromLatestRelease(
			Map<String, String>[] versions) {
		VersionDirectory version = null;
		for (int i = 0; i < versions.length; i++) {
			if (versions[i].get("type").equals("release")
					&& (version = fromVersionId(versions[i].get("id"))) != null) {
				return version;
			}
		}
		return null;
	}

	public static VersionDirectory fromLatestSnapshot(
			Map<String, String>[] versions) {
		VersionDirectory version = null;
		for (int i = 0; i < versions.length; i++) {
			if ((version = fromVersionId(versions[i].get("id"))) != null) {
				return version;
			}
		}
		return null;
	}

	public String getProfileName() {
		return profile.getName();
	}

	public String getGameDir() {
		return profile.getGameDir();
	}

	public Status getStatus() {
		return status;
	}

	public File getJarFile() {
		return version.getJar();
	}
}
