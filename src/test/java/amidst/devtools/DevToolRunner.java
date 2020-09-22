package amidst.devtools;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import amidst.AmidstVersion;
import amidst.ResourceLoader;
import amidst.devtools.settings.DevToolSettings;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.versionfeatures.DefaultVersionFeatures;
import amidst.mojangapi.world.versionfeatures.FeatureKey;
import amidst.parsing.FormatException;
import amidst.settings.biomeprofile.BiomeProfile;

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
		new MinecraftVersionCompatibilityChecker(versionsDirectory(), librariesDirectory(), versionList()).run();
	}

	@Ignore
	@Test
	public void generateBiomeColorImages() throws IOException {
		new GenerateBiomeColorImages(DefaultVersionFeatures.builder(null, null).create(RecognisedVersion.UNKNOWN).get(FeatureKey.BIOME_LIST).iterable(), Paths.get(biomeColorImagesDirectory())).run();
	}

	@Ignore
	@Test
	public void benchmarkWorldGeneration() throws FormatException, IOException {
		new WorldGenerationBencher(benchmarksDirectory(), versionsDirectory(), librariesDirectory(), versionList()).run();
	}
	
	@Ignore
	@Test
	public void serializeBiomeProfile() {
		new BiomeProfileSerializer(BiomeProfile.createExampleProfile()).run();
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

	private String benchmarksDirectory() {
		return DevToolSettings.INSTANCE.getBenchmarksDirectory();
	}

	private AmidstVersion amidstVersion() {
		return AmidstVersion.from(ResourceLoader.getProperties("/amidst/metadata.properties"));
	}
}
