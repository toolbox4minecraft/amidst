package amidst.mojangapi.dotminecraft;

import java.io.File;
import java.util.List;

import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;

public class DotMinecraftDirectory {
	private final File dotMinecraft;
	private final File libraries;
	private final File saves;
	private final File versions;
	private final File launcherProfilesDotJson;
	private final List<SaveDirectory> saveDirectories;
	private final List<VersionDirectory> versionDirectories;
	private final List<ProfileDirectory> profileDirectories;
	private final LauncherProfilesJson launcherProfilesJson;

	DotMinecraftDirectory(File dotMinecraft, File libraries, File saves,
			File versions, File launcherProfilesDotJson,
			List<SaveDirectory> saveDirectories,
			List<VersionDirectory> versionDirectories,
			List<ProfileDirectory> profileDirectories,
			LauncherProfilesJson launcherProfilesJson) {
		this.dotMinecraft = dotMinecraft;
		this.libraries = libraries;
		this.saves = saves;
		this.versions = versions;
		this.launcherProfilesDotJson = launcherProfilesDotJson;
		this.saveDirectories = saveDirectories;
		this.versionDirectories = versionDirectories;
		this.profileDirectories = profileDirectories;
		this.launcherProfilesJson = launcherProfilesJson;
	}

	public File getDotMinecraft() {
		return dotMinecraft;
	}

	public File getLibraries() {
		return libraries;
	}

	public File getSaves() {
		return saves;
	}

	public File getVersions() {
		return versions;
	}

	public File getLauncherProfilesDotJson() {
		return launcherProfilesDotJson;
	}

	public List<SaveDirectory> getSaveDirectories() {
		return saveDirectories;
	}

	public List<VersionDirectory> getVersionDirectories() {
		return versionDirectories;
	}

	public List<ProfileDirectory> getProfileDirectories() {
		return profileDirectories;
	}

	public LauncherProfilesJson getLauncherProfilesJson() {
		return launcherProfilesJson;
	}
}
