package amidst.minecraft;

import java.io.File;

import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;

// TODO: make this non-static
@Deprecated
public class LocalMinecraftInstallation {
	private static DotMinecraftDirectory dotMinecraftDirectory;

	public static void set(DotMinecraftDirectory dotMinecraftDirectory) {
		LocalMinecraftInstallation.dotMinecraftDirectory = dotMinecraftDirectory;

	}

	public static File getMinecraftLibraries() {
		return dotMinecraftDirectory.getLibraries();
	}
}
