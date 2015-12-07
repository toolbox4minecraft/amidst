package amidst.preferences;

import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;

public class ProfileSelection {
	private volatile LaucherProfileJson profile;
	private volatile VersionDirectory versionDirectory;

	public void setLaucherProfileJson(LaucherProfileJson profile) {
		this.profile = profile;
	}

	public void setVersionDirectory(VersionDirectory versionDirectory) {
		this.versionDirectory = versionDirectory;
	}

	public LaucherProfileJson getLaucherProfileJson() {
		return profile;
	}

	public VersionDirectory getVersionDirectory() {
		return versionDirectory;
	}
}
