package amidst.preferences;

import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.launcherprofiles.LauncherProfileJson;

public class ProfileSelection {
	private volatile LauncherProfileJson profile;
	private volatile VersionDirectory versionDirectory;

	public void setLaucherProfileJson(LauncherProfileJson profile) {
		this.profile = profile;
	}

	public void setVersionDirectory(VersionDirectory versionDirectory) {
		this.versionDirectory = versionDirectory;
	}

	public LauncherProfileJson getLaucherProfileJson() {
		return profile;
	}

	public VersionDirectory getVersionDirectory() {
		return versionDirectory;
	}
}
