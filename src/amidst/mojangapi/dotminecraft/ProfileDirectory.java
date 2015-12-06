package amidst.mojangapi.dotminecraft;

import java.io.File;

import amidst.mojangapi.launcherprofiles.LaucherProfileJson;

public class ProfileDirectory {
	private final File profile;
	private final File saves;
	private final LaucherProfileJson profileJson;

	public ProfileDirectory(File profile, File saves,
			LaucherProfileJson profileJson) {
		this.profile = profile;
		this.saves = saves;
		this.profileJson = profileJson;
	}

	public File getProfile() {
		return profile;
	}

	public LaucherProfileJson getProfileJson() {
		return profileJson;
	}

	public File getSaves() {
		return saves;
	}
}
