package amidst.mojangapi.dotminecraft;

import java.io.File;

public class ProfileDirectory {
	private final File profile;
	private final SavesDirectory saves;

	public ProfileDirectory(File profile) {
		this.profile = profile;
		this.saves = new SavesDirectory(new File(profile, "saves"));
	}

	public File getProfile() {
		return profile;
	}

	public SavesDirectory getSaves() {
		return saves;
	}
}
