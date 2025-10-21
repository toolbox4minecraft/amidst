package amidst.mojangapi.file;

import amidst.ResourceLoader;
import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data representing a version of Minecraft.
 */
@Immutable
public class Version {
	private final VersionListEntryJson versionListEntryJson;

	public Version(VersionListEntryJson versionListEntryJson) {
		this.versionListEntryJson = versionListEntryJson;
	}

	/**
	 * Gets the version list provided by the Mojang API.
	 *
	 * @return a list of versions
	 * @throws FormatException if the JSON parser fails
	 * @throws IOException     if the JSON parser fails
	 */
	public static List<Version> newRemoteVersionList() throws FormatException, IOException {
		return JsonReader
				.readLocation("https://launchermeta.mojang.com/mc/game/version_manifest.json", VersionListJson.class)
				.getVersions()
				.stream()
				.map(Version::new)
				.collect(Collectors.toList());
	}

	/**
	 * Gets the version list available on disk.
	 *
	 * @return a list of versions
	 * @throws FormatException if the JSON parser fails
	 * @throws IOException     if the JSON parser fails
	 */
	public static List<Version> newLocalVersionList() throws FormatException, IOException {
		return JsonReader
				.readLocation(ResourceLoader.getResourceURL("/amidst/mojangapi/version_manifest.json"), VersionListJson.class)
				.getVersions()
				.stream()
				.map(Version::new)
				.collect(Collectors.toList());
	}

	public String getId() {
		return versionListEntryJson.getId();
	}

	public ReleaseType getType() {
		return versionListEntryJson.getType();
	}

	public RemoteVersion fetchRemoteVersion() throws FormatException, IOException {
		return RemoteVersion.from(versionListEntryJson.getMetaUrl());
	}

	/*public boolean hasServer() {
		return downloadService.hasServer(versionListEntryJson.getId());
	}

	public boolean hasClient() {
		return downloadService.hasClient(versionListEntryJson.getId());
	}

	public void downloadServer(String prefix) throws IOException {
		downloadService.downloadServer(prefix, versionListEntryJson.getId());
	}

	public void downloadClient(String prefix) throws IOException {
		downloadService.downloadClient(prefix, versionListEntryJson.getId());
	}*/

	public Path getClientJarFile(Path prefix) {
		String versionId = versionListEntryJson.getId();
		return prefix.resolve(versionId + "/" + versionId + ".jar");
	}

	public Path getClientJsonFile(Path prefix) {
		String versionId = versionListEntryJson.getId();
		return prefix.resolve(versionId + "/" + versionId + ".json");
	}
}
