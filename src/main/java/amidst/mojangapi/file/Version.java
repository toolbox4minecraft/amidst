package amidst.mojangapi.file;

import java.io.IOException;
import java.nio.file.Path;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.service.FilenameService;
import amidst.parsing.FormatException;

@Immutable
public class Version {
	private final FilenameService filenameService = new FilenameService();
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
		return RemoteVersion.from(filenameService, versionListEntryJson.getMetaUrl());
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
		return filenameService.getClientJarFile(prefix, versionListEntryJson.getId());
	}

	public Path getClientJsonFile(Path prefix) {
		return filenameService.getClientJsonFile(prefix, versionListEntryJson.getId());
	}
}
