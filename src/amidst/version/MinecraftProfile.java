package amidst.version;

import java.io.File;

import amidst.mojangapi.ReleaseType;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;

public class MinecraftProfile {
	public enum Status {
		IDLE("scanning"), MISSING("missing"), FAILED("failed"), FOUND("found");

		private final String name;

		private Status(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private MinecraftVersion version;
	private final LaucherProfileJson profile;
	private final LatestVersionList latestVersionList;

	private Status status = Status.IDLE;
	private String versionName = "unknown";

	public MinecraftProfile(LaucherProfileJson profile,
			LatestVersionList latestVersionList) {
		this.profile = profile;
		this.latestVersionList = latestVersionList;
		if (profile.hasLastVersionId()) {
			version = MinecraftVersion
					.fromVersionId(profile.getLastVersionId());
			if (version == null) {
				status = Status.MISSING;
				return;
			}
			status = Status.FOUND;
			versionName = version.getName();
		} else {
			onLoadStateChange();
		}
	}

	public void onLoadStateChange() {
		switch (latestVersionList.getLoadState()) {
		case FAILED:
			status = Status.FAILED;
			break;
		case IDLE:
			status = Status.IDLE;
			break;
		case LOADED:
			status = Status.FOUND;
			if (profile.isAllowed(ReleaseType.SNAPSHOT)) {
				version = MinecraftVersion.fromLatestSnapshot(latestVersionList
						.getVersions());
			} else {
				version = MinecraftVersion.fromLatestRelease(latestVersionList
						.getVersions());
			}
			if (version == null) {
				status = Status.FAILED;
			} else {
				versionName = version.getName();
			}
			break;
		case LOADING:
			status = Status.IDLE;
			break;
		}
	}

	public String getProfileName() {
		return profile.getName();
	}

	public String getGameDir() {
		return profile.getGameDir();
	}

	public String getVersionName() {
		return versionName;
	}

	public Status getStatus() {
		return status;
	}

	public File getJarFile() {
		return version.getJarFile();
	}
}
