package amidst.devtools;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import amidst.clazz.translator.ClassTranslator;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.facade.LauncherProfile;
import amidst.mojangapi.file.facade.MinecraftInstallation;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.file.service.DownloadService;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.local.DefaultClassTranslator;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterface;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.testworld.TestWorldCache;
import amidst.mojangapi.world.testworld.TestWorldDeclaration;

public class GenerateWorldTestData {
	private final String prefix;
	private final VersionListJson versionList;
	private final MinecraftInstallation minecraftInstallation;
	private final List<String> failed = new LinkedList<>();
	private final List<String> successful = new LinkedList<>();

	public GenerateWorldTestData(String prefix, String libraries, VersionListJson versionList)
			throws DotMinecraftDirectoryNotFoundException {
		this.prefix = prefix;
		this.versionList = versionList;
		this.minecraftInstallation = MinecraftInstallation
				.newCustomMinecraftInstallation(new File(libraries), null, new File(prefix), null);
	}

	public void run() {
		for (VersionListEntryJson version : versionList.getVersions()) {
			for (TestWorldDeclaration declaration : TestWorldDeclaration.values()) {
				if (declaration.getRecognisedVersion().getName().equals(version.getId())) {
					generate(declaration, version);
				}
			}
		}
		print("============ Successful ============", successful);
		print("============== Failed ==============", failed);
	}

	private void generate(TestWorldDeclaration declaration, VersionListEntryJson version) {
		String versionId = version.getId();
		if (new DownloadService().tryDownloadClient(prefix, version)) {
			try {
				ClassTranslator translator = DefaultClassTranslator.INSTANCE.get();
				LauncherProfile launcherProfile = minecraftInstallation.newLauncherProfile(versionId);
				TestWorldCache.createAndPut(declaration, LocalMinecraftInterface.create(translator, launcherProfile));
				successful.add(versionId);
			} catch (
					LocalMinecraftInterfaceCreationException
					| MinecraftInterfaceException
					| IOException
					| MojangApiParsingException e) {
				e.printStackTrace();
				failed.add(versionId);
			}
		} else {
			failed.add(versionId);
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
