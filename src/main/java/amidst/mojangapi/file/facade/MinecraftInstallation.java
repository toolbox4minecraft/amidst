package amidst.mojangapi.file.facade;

import java.io.File;

import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.service.DotMinecraftDirectoryService;

public class MinecraftInstallation {
	public static MinecraftInstallation newCustomMinecraftInstallation(
			File libraries,
			File saves,
			File versions,
			File launcherProfilesJson) throws DotMinecraftDirectoryNotFoundException {
		DotMinecraftDirectory dotMinecraftDirectory = new DotMinecraftDirectoryService()
				.createCustomDotMinecraftDirectory(libraries, saves, versions, launcherProfilesJson);
		return new MinecraftInstallation(dotMinecraftDirectory);
	}

	public static MinecraftInstallation newLocalMinecraftInstallation(String preferredDotMinecraftDirectory)
			throws DotMinecraftDirectoryNotFoundException {
		DotMinecraftDirectory dotMinecraftDirectory = new DotMinecraftDirectoryService()
				.createDotMinecraftDirectory(preferredDotMinecraftDirectory);
		return new MinecraftInstallation(dotMinecraftDirectory);
	}

	private final DotMinecraftDirectory dotMinecraftDirectory;

	public MinecraftInstallation(DotMinecraftDirectory dotMinecraftDirectory) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
	}

	public DotMinecraftDirectory getDotMinecraftDirectory() {
		return dotMinecraftDirectory;
	}
}
