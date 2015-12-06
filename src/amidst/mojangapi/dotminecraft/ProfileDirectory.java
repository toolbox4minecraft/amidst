package amidst.mojangapi.dotminecraft;

import java.io.File;

import amidst.mojangapi.launcherprofiles.LaucherProfileJson;

public class ProfileDirectory {
	private final LaucherProfileJson profile;
	private final SavesDirectory saves;

	public ProfileDirectory(LaucherProfileJson profile) {
		this.profile = profile;
		this.saves = new SavesDirectory(new File(profile.getGameDir(), "saves"));
	}

	public LaucherProfileJson getProfileJson() {
		return profile;
	}

	public SavesDirectory getSavesDirectory() {
		return saves;
	}
}
