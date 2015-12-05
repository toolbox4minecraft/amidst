package amidst.mojangapi.dotminecraft;

import java.io.File;

public class DotMinecraftDirectory {
	private final File dotMinecraft;
	private final File libraries;
	private final SavesDirectory saves;
	private final File versions;
	private final File launcherProfilesJson;

	public DotMinecraftDirectory(File dotMinecraft) {
		this.dotMinecraft = dotMinecraft;
		this.libraries = new File(dotMinecraft, "libraries");
		this.saves = new SavesDirectory(new File(dotMinecraft, "saves"));
		this.versions = new File(dotMinecraft, "versions");
		this.launcherProfilesJson = new File(dotMinecraft,
				"launcher_profiles.json");
	}

	public File getDotMinecraft() {
		return dotMinecraft;
	}

	public File getLibraries() {
		return libraries;
	}

	public SavesDirectory getSaves() {
		return saves;
	}

	public File getVersions() {
		return versions;
	}

	public File getLauncherProfilesJson() {
		return launcherProfilesJson;
	}
}
