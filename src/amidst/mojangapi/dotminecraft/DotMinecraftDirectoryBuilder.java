package amidst.mojangapi.dotminecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import amidst.mojangapi.MojangAPI;
import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;

public class DotMinecraftDirectoryBuilder {
	private final File dotMinecraft;
	private File libraries;
	private File saves;
	private File versions;
	private File launcherProfilesDotJson;
	private Map<String, VersionDirectory> versionDirectories;
	private List<ProfileDirectory> profileDirectories;
	private LauncherProfilesJson launcherProfilesJson;

	public DotMinecraftDirectoryBuilder(File dotMinecraft) {
		this.dotMinecraft = dotMinecraft;
		this.libraries = new File(dotMinecraft, "libraries");
		this.saves = new File(dotMinecraft, "saves");
		this.versions = new File(dotMinecraft, "versions");
		this.launcherProfilesDotJson = new File(dotMinecraft,
				"launcher_profiles.json");
		this.versionDirectories = Collections.emptyMap();
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

	public DotMinecraftDirectoryBuilder loadVersionDirectories() {
		this.versionDirectories = MojangAPI.createVersionDirectories(versions);
		return this;
	}

	public DotMinecraftDirectoryBuilder loadProfileDirectories() {
		if (launcherProfilesJson == null) {
			throw new IllegalStateException(
					"You need to call the method loadLauncherProfilesJson() first.");
		}
		this.profileDirectories = MojangAPI
				.createProfileDirectories(launcherProfilesJson);
		return this;
	}

	public DotMinecraftDirectoryBuilder loadLauncherProfilesJson()
			throws FileNotFoundException, IOException {
		this.launcherProfilesJson = MojangAPI
				.launcherProfilesFrom(launcherProfilesDotJson);
		return this;
	}

	public DotMinecraftDirectory construct() {
		return new DotMinecraftDirectory(dotMinecraft, libraries, saves,
				versions, launcherProfilesDotJson, versionDirectories,
				profileDirectories, launcherProfilesJson);
	}
}
