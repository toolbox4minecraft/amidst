package amidst.devtools;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import amidst.AmidstVersion;
import amidst.ResourceLoader;
import amidst.devtools.settings.DevToolSettings;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.world.biome.Biome;
import amidst.parsing.FormatException;

/**
 * Eclipse does not allow to run the main directly as a Java Application,
 * because it is placed in the test directory. Ensure that these tests are
 * ignored when creating a commit, so they will be ignored by travis ci.
 */
public class DevToolRunner {
	@Ignore
	@Test
	public void generateInstalledVersionsList() throws FormatException, IOException {
		new GenerateInstalledVersionsList(MinecraftInstallation.newLocalMinecraftInstallation()).run();
	}

	@Ignore
	@Test
	public void generateRecognisedVersionList() throws FormatException, IOException {
		new GenerateRecognisedVersionList(versionsDirectory(), librariesDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void generateUpdateInformationJson() {
		new GenerateUpdateInformationJson(amidstVersion()).run();
	}

	@Ignore
	@Test
	public void generateWorldTestData() throws FormatException, IOException {
		new GenerateWorldTestData(versionsDirectory(), librariesDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void checkMinecraftJarFileDownloadAvailability() throws FormatException, IOException {
		new MinecraftJarDownloadAvailabilityChecker(versionList()).run();
	}

	@Ignore
	@Test
	public void downloadMinecraftJarFiles() throws FormatException, IOException {
		new MinecraftJarDownloader(versionsDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void checkMinecraftVersionCompatibility() throws FormatException, IOException {
		new MinecraftVersionCompatibilityChecker(versionsDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void generateBiomeColorImages() throws IOException {
		new GenerateBiomeColorImages(Biome.allBiomes(), new File(biomeColorImagesDirectory())).run();
	}

	private VersionList versionList() throws FormatException, IOException {
		return VersionList.newRemoteVersionList();
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
