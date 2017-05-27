package amidst.devtools;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import amidst.clazz.translator.ClassTranslator;
import amidst.mojangapi.file.DotMinecraftDirectoryService;
import amidst.mojangapi.file.FilenameService;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.local.DefaultClassTranslator;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterface;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.testworld.TestWorldCache;
import amidst.mojangapi.world.testworld.TestWorldDeclaration;

public class GenerateWorldTestData {
	private final String prefix;
	private final File libraries;
	private final VersionListJson versionList;
	private final File versions;
	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final List<String> failed = new LinkedList<>();
	private final List<String> successful = new LinkedList<>();

	public GenerateWorldTestData(String prefix, String libraries, VersionListJson versionList) {
		this.prefix = prefix;
		this.libraries = new File(libraries);
		this.versionList = versionList;
		this.versions = new File(prefix);
		this.dotMinecraftDirectory = new DotMinecraftDirectory(null, this.libraries);
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
		if (new DotMinecraftDirectoryService().tryDownloadClient(prefix, version)) {
			try {
				ClassTranslator translator = DefaultClassTranslator.INSTANCE.get();
				VersionDirectory versionDirectory = createVersionDirectory(versionId);
				TestWorldCache.createAndPut(
						declaration,
						LocalMinecraftInterface.create(translator, versionDirectory, dotMinecraftDirectory));
				successful.add(versionId);
			} catch (LocalMinecraftInterfaceCreationException | MinecraftInterfaceException | IOException e) {
				e.printStackTrace();
				failed.add(versionId);
			}
		} else {
			failed.add(versionId);
		}
	}

	private VersionDirectory createVersionDirectory(String versionId) {
		FilenameService filenameService = new FilenameService();
		File jar = filenameService.getClientJarFile(versions, versionId);
		File json = filenameService.getClientJsonFile(versions, versionId);
		return new VersionDirectory(versionId, jar, json);
	}

	private void print(String title, Iterable<String> lines) {
		System.out.println(title);
		for (String line : lines) {
			System.out.println(line);
		}
		System.out.println();
	}
}
