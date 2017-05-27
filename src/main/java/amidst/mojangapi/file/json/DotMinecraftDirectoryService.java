package amidst.mojangapi.file.json;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.FilenameService;
import amidst.mojangapi.file.URIUtils;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

@Immutable
public class DotMinecraftDirectoryService {
	private final FilenameService filenameService = new FilenameService();

	public VersionDirectory tryFindFirstValidVersionDirectory(
			List<ReleaseType> allowedReleaseTypes,
			MojangApi mojangApi) throws FileNotFoundException {
		VersionListJson versionListJson = mojangApi.getVersionList();

		for (VersionListEntryJson version : versionListJson.getVersions()) {
			if (allowedReleaseTypes.contains(version.getType())) {
				VersionDirectory versionDirectory = version.createVersionDirectory(mojangApi);
				if (versionDirectory.isValid()) {
					return versionDirectory;
				}
			}
		}
		return null;
	}

	public boolean hasServer(VersionListEntryJson version) {
		return URIUtils.exists(filenameService.getRemoteServerJar(version.getId()));
	}

	public boolean hasClient(VersionListEntryJson version) {
		return URIUtils.exists(filenameService.getRemoteClientJar(version.getId()));
	}

	public void downloadServer(String prefix, VersionListEntryJson version) throws IOException {
		URIUtils.download(
				filenameService.getRemoteServerJar(version.getId()),
				filenameService.getServerJar(prefix, version.getId()));
	}

	public void downloadClient(String prefix, VersionListEntryJson version) throws IOException {
		URIUtils.download(
				filenameService.getRemoteClientJar(version.getId()),
				filenameService.getClientJar(prefix, version.getId()));
		URIUtils.download(
				filenameService.getRemoteClientJson(version.getId()),
				filenameService.getClientJson(prefix, version.getId()));
	}

	public boolean tryDownloadServer(String prefix, VersionListEntryJson version) {
		try {
			downloadServer(prefix, version);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download server: " + version.getId());
		}
		return false;
	}

	public boolean tryDownloadClient(String prefix, VersionListEntryJson version) {
		try {
			downloadClient(prefix, version);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download client: " + version.getId());
		}
		return false;
	}
}
