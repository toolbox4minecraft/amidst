package amidst.mojangapi.dotminecraft;

import java.io.File;
import java.util.List;

import amidst.mojangapi.launcherprofiles.LaucherProfileJson;

public class ProfileDirectory {
	private final File profile;
	private final File saves;
	private final LaucherProfileJson profileJson;
	private final List<SaveDirectory> saveDirectories;

	public ProfileDirectory(File profile, File saves,
			LaucherProfileJson profileJson, List<SaveDirectory> saveDirectories) {
		this.profile = profile;
		this.saves = saves;
		this.profileJson = profileJson;
		this.saveDirectories = saveDirectories;
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

	public List<SaveDirectory> getSaveDirectories() {
		return saveDirectories;
	}
}
