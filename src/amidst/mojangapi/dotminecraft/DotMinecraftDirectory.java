package amidst.mojangapi.dotminecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amidst.logging.Log;
import amidst.mojangapi.FilenameFactory;
import amidst.mojangapi.MojangAPI;
import amidst.mojangapi.launcherprofiles.LauncherProfiles;

public class DotMinecraftDirectory {
	private final File dotMinecraft;
	private final File libraries;
	private final SavesDirectory saves;
	private final List<VersionDirectory> versions;
	private final LauncherProfiles launcherProfilesJson;

	public DotMinecraftDirectory(File dotMinecraft)
			throws FileNotFoundException, IOException {
		this.dotMinecraft = dotMinecraft;
		this.libraries = new File(dotMinecraft, "libraries");
		this.saves = new SavesDirectory(new File(dotMinecraft, "saves"));
		this.versions = loadVersions(new File(dotMinecraft, "versions"));
		this.launcherProfilesJson = MojangAPI.launcherProfilesFrom(new File(
				dotMinecraft, "launcher_profiles.json"));
	}

	private List<VersionDirectory> loadVersions(File versions) {
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
			return new VersionDirectory(jar, json);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load minecraft version.", e);
		}
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

	public List<VersionDirectory> getVersions() {
		return versions;
	}

	public LauncherProfiles getLauncherProfilesJson() {
		return launcherProfilesJson;
	}
}
