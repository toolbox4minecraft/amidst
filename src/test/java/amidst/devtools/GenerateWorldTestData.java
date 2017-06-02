package amidst.devtools;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import amidst.clazz.translator.ClassTranslator;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.local.DefaultClassTranslator;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterface;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
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
				.newCustomMinecraftInstallation(new File(libraries), null, new File(prefix), null);
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
		if (version.tryDownloadClient(prefix)) {
			try {
				ClassTranslator translator = DefaultClassTranslator.INSTANCE.get();
				LauncherProfile launcherProfile = minecraftInstallation.newLauncherProfile(version.getId());
				TestWorldCache.createAndPut(declaration, LocalMinecraftInterface.create(translator, launcherProfile));
				successful.add(version.getId());
			} catch (
					LocalMinecraftInterfaceCreationException
					| MinecraftInterfaceException
					| FormatException
					| IOException e) {
				e.printStackTrace();
				failed.add(version.getId());
			}
		} else {
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
