package amidst.mojangapi.file.json.versionlist;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject(ignoreUnknown=true)
public class VersionListJson {
	private volatile List<VersionListEntryJson> versions = Collections.emptyList();

	public VersionListJson() {
	}

	public List<VersionListEntryJson> getVersions() {
		return versions;
	}
}
