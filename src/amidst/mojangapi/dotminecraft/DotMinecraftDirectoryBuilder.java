package amidst.mojangapi.dotminecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amidst.logging.Log;
import amidst.mojangapi.FilenameFactory;
import amidst.mojangapi.MojangAPI;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;
import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;

public class DotMinecraftDirectoryBuilder {
	private final File dotMinecraft;
	private File libraries;
	private File saves;
	private File versions;
	private File launcherProfilesDotJson;
	private List<SaveDirectory> saveDirectories;
	private List<VersionDirectory> versionDirectories;
	private List<ProfileDirectory> profileDirectories;
	private LauncherProfilesJson launcherProfilesJson;

	public DotMinecraftDirectoryBuilder(File dotMinecraft) {
		this.dotMinecraft = dotMinecraft;
		this.libraries = new File(dotMinecraft, "libraries");
		this.saves = new File(dotMinecraft, "saves");
		this.versions = new File(dotMinecraft, "versions");
		this.launcherProfilesDotJson = new File(dotMinecraft,
				"launcher_profiles.json");
		this.versionDirectories = Collections.emptyList();
		this.profileDirectories = Collections.emptyList();
	}

	public DotMinecraftDirectoryBuilder setLibraries(File libraries) {
		this.libraries = libraries;
		return this;
	}

	public DotMinecraftDirectoryBuilder setSaves(File saves) {
		this.saves = saves;
		return this;
	}

	public DotMinecraftDirectoryBuilder setVersions(File versions) {
		this.versions = versions;
		return this;
	}

	public DotMinecraftDirectoryBuilder setLauncherProfilesJson(
			File launcherProfilesJson) {
		this.launcherProfilesDotJson = launcherProfilesJson;
		return this;
	}

	public DotMinecraftDirectoryBuilder loadSaveDirectories() {
		this.saveDirectories = createSaveDirectories(saves);
		return this;
	}

	private List<SaveDirectory> createSaveDirectories(File saves) {
		List<SaveDirectory> result = new ArrayList<SaveDirectory>();
		for (File file : saves.listFiles()) {
			result.add(new SaveDirectory(file));
		}
		return result;
	}

	public DotMinecraftDirectoryBuilder loadVersionDirectories() {
		this.versionDirectories = createVersionDirectories(versions);
		return this;
	}

	private List<VersionDirectory> createVersionDirectories(File versions) {
		List<VersionDirectory> result = new ArrayList<VersionDirectory>();
		for (File file : versions.listFiles()) {
			try {
				result.add(tryCreateVersionDirectory(versions, file.getName()));
			} catch (RuntimeException e) {
				Log.w("Unable to load minecraft version.");
				e.printStackTrace();
			}
		}
		return result;
	}

	private VersionDirectory tryCreateVersionDirectory(File versions, String id) {
		File jar = FilenameFactory.getClientJarFile(versions, id);
		File json = FilenameFactory.getClientJsonFile(versions, id);
		try {
			return new VersionDirectory(jar, MojangAPI.versionFrom(json));
		} catch (Exception e) {
			throw new RuntimeException("Unable to load minecraft version.", e);
		}
	}

	public DotMinecraftDirectoryBuilder loadProfileDirectories() {
		if (launcherProfilesJson == null) {
			throw new IllegalStateException(
					"You need to call the method loadLauncherProfilesJson() first.");
		}
		this.profileDirectories = createProfileDirectories(launcherProfilesJson);
		return this;
	}

	private List<ProfileDirectory> createProfileDirectories(
			LauncherProfilesJson launcherProfilesJson) {
		ArrayList<ProfileDirectory> result = new ArrayList<ProfileDirectory>();
		for (LaucherProfileJson profile : launcherProfilesJson.getProfiles()) {
			result.add(createProfileDirectory(profile));
		}
		return result;
	}

	private ProfileDirectory createProfileDirectory(
			LaucherProfileJson profileJson) {
		File profile = new File(profileJson.getGameDir());
		File saves = new File(profile, "saves");
		List<SaveDirectory> saveDirectories = createSaveDirectories(saves);
		return new ProfileDirectory(profile, saves, profileJson,
				saveDirectories);
	}

	public DotMinecraftDirectoryBuilder loadLauncherProfilesJson()
			throws FileNotFoundException, IOException {
		this.launcherProfilesJson = MojangAPI
				.launcherProfilesFrom(launcherProfilesDotJson);
		return this;
	}

	public DotMinecraftDirectory construct() {
		return new DotMinecraftDirectory(dotMinecraft, libraries, saves,
				versions, launcherProfilesDotJson, saveDirectories,
				versionDirectories, profileDirectories, launcherProfilesJson);
	}
}
