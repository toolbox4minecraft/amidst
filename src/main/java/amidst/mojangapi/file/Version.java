package amidst.mojangapi.file;

import java.io.File;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.service.DownloadService;
import amidst.mojangapi.file.service.FilenameService;

@Immutable
public class Version {
	private final FilenameService filenameService = new FilenameService();
	private final DownloadService downloadService = new DownloadService();
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

	public boolean hasServer() {
		return downloadService.hasServer(versionListEntryJson.getId());
	}

	public boolean hasClient() {
		return downloadService.hasClient(versionListEntryJson.getId());
	}

	public boolean tryDownloadServer(String prefix) {
		return downloadService.tryDownloadServer(prefix, versionListEntryJson.getId());
	}

	public boolean tryDownloadClient(String prefix) {
		return downloadService.tryDownloadClient(prefix, versionListEntryJson.getId());
	}

	public void downloadServer(String prefix) throws IOException {
		downloadService.downloadServer(prefix, versionListEntryJson.getId());
	}

	public void downloadClient(String prefix) throws IOException {
		downloadService.downloadClient(prefix, versionListEntryJson.getId());
	}

	public File getClientJarFile(File prefix) {
		return filenameService.getClientJarFile(prefix, versionListEntryJson.getId());
	}

	public File getClientJsonFile(File prefix) {
		return filenameService.getClientJsonFile(prefix, versionListEntryJson.getId());
	}
}
