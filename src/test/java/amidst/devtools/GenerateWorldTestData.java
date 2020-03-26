package amidst.devtools;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaces;
import amidst.mojangapi.world.testworld.TestWorldCache;
import amidst.mojangapi.world.testworld.TestWorldDeclaration;
import amidst.parsing.FormatException;

public class GenerateWorldTestData {
	private final String prefix;
	private final VersionList versionList;
	private final MinecraftInstallation minecraftInstallation;
	private final List<String> failed = new LinkedList<>();
	private final List<String> successful = new LinkedList<>();

	public GenerateWorldTestData(String prefix, String libraries, VersionList versionList)
			throws DotMinecraftDirectoryNotFoundException {
		this.prefix = prefix;
		this.versionList = versionList;
		this.minecraftInstallation = MinecraftInstallation
				.newCustomMinecraftInstallation(Paths.get(libraries), null, Paths.get(prefix), null);
	}

	public void run() {
		for (Version version : versionList.getVersions()) {
			for (TestWorldDeclaration declaration : TestWorldDeclaration.values()) {
				if (declaration.getRecognisedVersion().getName().equals(version.getId())) {
					generate(declaration, version);
				}
			}
		}
		print("============ Successful ============", successful);
		print("============== Failed ==============", failed);
	}

	private void generate(TestWorldDeclaration declaration, Version version) {
		try {
			version.fetchRemoteVersion().downloadClient(prefix);
			LauncherProfile launcherProfile = minecraftInstallation.newLauncherProfile(version.getId());
			MinecraftInterface minecraftInterface = MinecraftInterfaces.fromLocalProfile(launcherProfile);
			TestWorldCache.createAndPut(declaration, minecraftInterface);
			successful.add(version.getId());
		} catch (
				MinecraftInterfaceCreationException
				| MinecraftInterfaceException
				| FormatException
				| IOException e) {
			e.printStackTrace();
			failed.add(version.getId());
		}
	}

	private void print(String title, Iterable<String> lines) {
		System.out.println(title);
		for (String line : lines) {
			System.out.println(line);
		}
		System.out.println();
	}
}
