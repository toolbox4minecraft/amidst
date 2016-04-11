package amidst.mojangapi.file.directory;

import java.io.File;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;

@Immutable
public class DotMinecraftDirectory {
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

	public DotMinecraftDirectory(File root, File libraries) {
		this.root = root;
		this.libraries = libraries;
		this.saves = new File(root, "saves");
		this.versions = new File(root, "versions");
		this.launcherProfilesJson = new File(root, "launcher_profiles.json");
	}

	public boolean isValid() {
		return root.isDirectory() && libraries.isDirectory() && launcherProfilesJson.isFile();
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

	@NotNull
	public LauncherProfilesJson readLauncherProfilesJson() throws MojangApiParsingException, IOException {
		return JsonReader.readLauncherProfilesFrom(launcherProfilesJson);
	}
}
