package amidst.devtools;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import amidst.AmidstVersion;
import amidst.ResourceLoader;
import amidst.devtools.settings.DevToolsSettings;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

/**
 * Eclipse does not allow to run the main directly as a Java Application,
 * because it is placed in the test directory. Ensure that these tests are
 * ignored when creating a commit, so they will be ignored by travis ci.
 */
public class DevToolRunner {
	@Ignore
	@Test
	public void generateRecognisedVersionList() throws IOException,
			MojangApiParsingException {
		new GenerateRecognisedVersionList(versionsDirectory(),
				librariesDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void generateUpdateInformationJson() throws IOException,
			MojangApiParsingException {
		new GenerateUpdateInformationJson(amidstVersion()).run();
	}

	@Ignore
	@Test
	public void generateWorldTestData() throws IOException,
			MojangApiParsingException {
		new GenerateWorldTestData(versionsDirectory(), librariesDirectory(),
				versionList()).run();
	}

	@Ignore
	@Test
	public void checkMinecraftJarFileDownloadAvailability() throws IOException,
			MojangApiParsingException {
		new MinecraftJarDownloadAvailabilityChecker(versionList()).run();
	}

	@Ignore
	@Test
	public void downloadMinecraftJarFiles() throws IOException,
			MojangApiParsingException {
		new MinecraftJarDownloader(versionsDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void checkMinecraftVersionCompatibility() throws IOException,
			MojangApiParsingException {
		new MinecraftVersionCompatibilityChecker(versionsDirectory(),
				versionList()).run();
	}

	private VersionListJson versionList() throws IOException,
			MojangApiParsingException {
		return JsonReader.readRemoteVersionList();
	}

	private String librariesDirectory() {
		return DevToolsSettings.INSTANCE.getMinecraftLibrariesDirectory();
	}

	private String versionsDirectory() {
		return DevToolsSettings.INSTANCE.getMinecraftVersionsDirectory();
	}

	private AmidstVersion amidstVersion() {
		return AmidstVersion.from(ResourceLoader
				.getProperties("/amidst/metadata.properties"));
	}
}
