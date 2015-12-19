package amidst.mojangapi.file.json.versionlist;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.ReleaseType;

@Immutable
public class VersionListJson {
	private volatile List<VersionListEntryJson> versions = Collections
			.emptyList();

	@GsonConstructor
	public VersionListJson() {
	}

	public List<VersionListEntryJson> getVersions() {
		return versions;
	}

	public VersionDirectory findFirstValidSnapshot(MojangApi mojangApi) {
		return findFirstValidVersionDirectory(mojangApi);
	}

	public VersionDirectory findFirstValidRelease(MojangApi mojangApi) {
		return findFirstValidVersionDirectory(mojangApi, ReleaseType.RELEASE);
	}

	public VersionDirectory findFirstValidVersionDirectory(MojangApi mojangApi) {
		for (VersionListEntryJson version : versions) {
			VersionDirectory versionDirectory = version
					.createVersionDirectory(mojangApi);
			if (versionDirectory.isValid()) {
				return versionDirectory;
			}
		}
		return null;
	}

	public VersionDirectory findFirstValidVersionDirectory(MojangApi mojangApi,
			ReleaseType releaseType) {
		for (VersionListEntryJson version : versions) {
			if (version.isType(releaseType)) {
				VersionDirectory versionDirectory = version
						.createVersionDirectory(mojangApi);
				if (versionDirectory.isValid()) {
					return versionDirectory;
				}
			}
		}
		return null;
	}
}
