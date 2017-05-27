package amidst.mojangapi.file.json;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.URIUtils;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

@Immutable
public class DotMinecraftDirectoryService {
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

	public boolean hasServer(VersionListEntryJson versionListEntryJson) {
		return URIUtils.exists(versionListEntryJson.getRemoteServerJar());
	}

	public boolean hasClient(VersionListEntryJson versionListEntryJson) {
		return URIUtils.exists(versionListEntryJson.getRemoteClientJar());
	}

	public void downloadServer(String prefix, VersionListEntryJson versionListEntryJson) throws IOException {
		URIUtils.download(versionListEntryJson.getRemoteServerJar(), versionListEntryJson.getServerJar(prefix));
	}

	public void downloadClient(String prefix, VersionListEntryJson versionListEntryJson) throws IOException {
		URIUtils.download(versionListEntryJson.getRemoteClientJar(), versionListEntryJson.getClientJar(prefix));
		URIUtils.download(versionListEntryJson.getRemoteClientJson(), versionListEntryJson.getClientJson(prefix));
	}

	public boolean tryDownloadServer(String prefix, VersionListEntryJson versionListEntryJson) {
		try {
			downloadServer(prefix, versionListEntryJson);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download server: " + versionListEntryJson.getId());
		}
		return false;
	}

	public boolean tryDownloadClient(String prefix, VersionListEntryJson versionListEntryJson) {
		try {
			downloadClient(prefix, versionListEntryJson);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download client: " + versionListEntryJson.getId());
		}
		return false;
	}
}
