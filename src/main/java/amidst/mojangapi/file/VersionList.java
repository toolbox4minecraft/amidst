package amidst.mojangapi.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.service.VersionListService;

@Immutable
public class VersionList {
	@Deprecated
	public static VersionList newRemoteOrLocalVersionList() throws FileNotFoundException {
		return new VersionList(
				new VersionListService()
						.readRemoteOrLocalVersionList()
						.getVersions()
						.stream()
						.map(v -> new Version(v))
						.collect(Collectors.toList()));
	}

	public static VersionList newRemoteVersionList() throws MojangApiParsingException, IOException {
		return new VersionList(
				new VersionListService()
						.readRemoteVersionList()
						.getVersions()
						.stream()
						.map(v -> new Version(v))
						.collect(Collectors.toList()));
	}

	public static VersionList newLocalVersionList() throws MojangApiParsingException, IOException {
		return new VersionList(
				new VersionListService()
						.readLocalVersionListFromResource()
						.getVersions()
						.stream()
						.map(v -> new Version(v))
						.collect(Collectors.toList()));
	}

	private final List<Version> versions;

	public VersionList(List<Version> versions) {
		this.versions = versions;
	}

	public List<Version> getVersions() {
		return versions;
	}
}
