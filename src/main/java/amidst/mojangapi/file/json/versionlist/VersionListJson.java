package amidst.mojangapi.file.json.versionlist;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class VersionListJson {
	private volatile List<VersionListEntryJson> versions = Collections.emptyList();

	@GsonConstructor
	public VersionListJson() {
	}

	public List<VersionListEntryJson> getVersions() {
		return versions;
	}
}
