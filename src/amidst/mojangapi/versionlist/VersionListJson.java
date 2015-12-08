package amidst.mojangapi.versionlist;

import java.util.Collections;
import java.util.List;

import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.internal.ReleaseType;

public class VersionListJson {
	private List<VersionListEntryJson> versions = Collections.emptyList();

	public VersionListJson() {
		// no-argument constructor for gson
	}

	public List<VersionListEntryJson> getVersions() {
		return versions;
	}

	public VersionDirectory findFirstValidSnapshot(
			DotMinecraftDirectory dotMinecraftDirectory) {
		return findFirstValidVersionDirectory(dotMinecraftDirectory);
	}

	public VersionDirectory findFirstValidRelease(
			DotMinecraftDirectory dotMinecraftDirectory) {
		return findFirstValidVersionDirectory(dotMinecraftDirectory,
				ReleaseType.RELEASE);
	}

	public VersionDirectory findFirstValidVersionDirectory(
			DotMinecraftDirectory dotMinecraftDirectory) {
		for (VersionListEntryJson version : versions) {
			VersionDirectory versionDirectory = version
					.createVersionDirectory(dotMinecraftDirectory);
			if (versionDirectory.isValid()) {
				return versionDirectory;
			}
		}
		return null;
	}

	public VersionDirectory findFirstValidVersionDirectory(
			DotMinecraftDirectory dotMinecraftDirectory, ReleaseType releaseType) {
		for (VersionListEntryJson version : versions) {
			if (version.isType(releaseType)) {
				VersionDirectory versionDirectory = version
						.createVersionDirectory(dotMinecraftDirectory);
				if (versionDirectory.isValid()) {
					return versionDirectory;
				}
			}
		}
		return null;
	}
}
