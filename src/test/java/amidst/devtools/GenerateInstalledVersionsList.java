package amidst.devtools;

import java.io.IOException;

import amidst.mojangapi.file.MinecraftInstallation;
import amidst.parsing.FormatException;

public class GenerateInstalledVersionsList {
	private final MinecraftInstallation minecraftInstallation;

	public GenerateInstalledVersionsList(MinecraftInstallation minecraftInstallation) {
		this.minecraftInstallation = minecraftInstallation;
	}

	public void run() throws FormatException, IOException {
		minecraftInstallation.readInstalledVersionsAsLauncherProfiles().stream().map(p -> p.getVersionName()).forEach(
				System.out::println);
	}
}
