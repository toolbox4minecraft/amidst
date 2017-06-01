package amidst.mojangapi.file;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.service.VersionListService;
import amidst.parsing.FormatException;

@Immutable
public class VersionList {
	public static VersionList newRemoteVersionList() throws FormatException, IOException {
		return new VersionList(
				new VersionListService()
						.readRemoteVersionList()
						.getVersions()
						.stream()
						.map(v -> new Version(v))
						.collect(Collectors.toList()));
	}

	public static VersionList newLocalVersionList() throws FormatException, IOException {
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
