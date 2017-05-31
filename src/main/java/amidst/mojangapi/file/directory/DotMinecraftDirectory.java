package amidst.mojangapi.file.directory;

import java.io.File;
import java.util.Objects;

import amidst.documentation.Immutable;

@Immutable
public class DotMinecraftDirectory {
	/**
	 * Allows to customize all parts of the .minecraft directory, mainly for
	 * testing and the dev tools. Pass null to use default values.
	 */
	public static DotMinecraftDirectory newCustom(
			File root,
			File libraries,
			File saves,
			File versions,
			File launcherProfilesJson) {
		Objects.requireNonNull(root);
		return new DotMinecraftDirectory(
				root,
				libraries != null ? libraries : new File(root, "libraries"),
				saves != null ? saves : new File(root, "saves"),
				versions != null ? versions : new File(root, "versions"),
				launcherProfilesJson != null ? launcherProfilesJson : new File(root, "launcher_profiles.json"));
	}

	private final File root;
	private final File libraries;
	private final File saves;
	private final File versions;
	private final File launcherProfilesJson;

	public DotMinecraftDirectory(File root) {
		this.root = root;
		this.libraries = new File(root, "libraries");
		this.saves = new File(root, "saves");
		this.versions = new File(root, "versions");
		this.launcherProfilesJson = new File(root, "launcher_profiles.json");
	}

	private DotMinecraftDirectory(File root, File libraries, File saves, File versions, File launcherProfilesJson) {
		this.root = root;
		this.libraries = libraries;
		this.saves = saves;
		this.versions = versions;
		this.launcherProfilesJson = launcherProfilesJson;
	}

	public boolean isValid() {
		return root.isDirectory() && libraries.isDirectory() && versions.isDirectory() && launcherProfilesJson.isFile();
	}

	public ProfileDirectory asProfileDirectory() {
		return new ProfileDirectory(root);
	}

	public File getRoot() {
		return root;
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

	public File getLauncherProfilesJson() {
		return launcherProfilesJson;
	}
}
