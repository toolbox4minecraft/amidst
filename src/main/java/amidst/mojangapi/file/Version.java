package amidst.mojangapi.file;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.parsing.FormatException;

import java.io.IOException;
import java.nio.file.Path;

@Immutable
public class Version {
	private final VersionListEntryJson versionListEntryJson;

	public Version(VersionListEntryJson versionListEntryJson) {
		this.versionListEntryJson = versionListEntryJson;
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
