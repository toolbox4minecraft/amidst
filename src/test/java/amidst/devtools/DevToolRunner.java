package amidst.devtools;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import amidst.AmidstVersion;
import amidst.ResourceLoader;
import amidst.devtools.settings.DevToolSettings;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.world.biome.Biome;

/**
 * Eclipse does not allow to run the main directly as a Java Application,
 * because it is placed in the test directory. Ensure that these tests are
 * ignored when creating a commit, so they will be ignored by travis ci.
 */
public class DevToolRunner {
	@Ignore
	@Test
	public void generateRecognisedVersionList() throws IOException, MojangApiParsingException {
		new GenerateRecognisedVersionList(versionsDirectory(), librariesDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void generateUpdateInformationJson() throws IOException, MojangApiParsingException {
		new GenerateUpdateInformationJson(amidstVersion()).run();
	}

	@Ignore
	@Test
	public void generateWorldTestData() throws IOException, MojangApiParsingException {
		new GenerateWorldTestData(versionsDirectory(), librariesDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void checkMinecraftJarFileDownloadAvailability() throws IOException, MojangApiParsingException {
		new MinecraftJarDownloadAvailabilityChecker(versionList()).run();
	}

	@Ignore
	@Test
	public void downloadMinecraftJarFiles() throws IOException, MojangApiParsingException {
		new MinecraftJarDownloader(versionsDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void checkMinecraftVersionCompatibility() throws IOException, MojangApiParsingException {
		new MinecraftVersionCompatibilityChecker(versionsDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void generateBiomeColorImages() throws IOException {
		new GenerateBiomeColorImages(Biome.allBiomes(), new File(biomeColorImagesDirectory())).run();
	}

	private VersionListJson versionList() throws IOException, MojangApiParsingException {
		return JsonReader.readRemoteVersionList();
	}

	private String librariesDirectory() {
		return DevToolSettings.INSTANCE.getMinecraftLibrariesDirectory();
	}

	private String versionsDirectory() {
		return DevToolSettings.INSTANCE.getMinecraftVersionsDirectory();
	}

	private String biomeColorImagesDirectory() {
		return DevToolSettings.INSTANCE.getBiomeColorImagesDirectory();
	}

	private AmidstVersion amidstVersion() {
		return AmidstVersion.from(ResourceLoader.getProperties("/amidst/metadata.properties"));
	}
}
