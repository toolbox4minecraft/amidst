package amidst.mojangapi.file.json.version;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject(ignoreUnknown=true)
public class VersionJson {
	private volatile String id;
	private volatile String inheritsFrom;
	private volatile List<LibraryJson> libraries = Collections.emptyList();

	public VersionJson() {
	}

	public String getId() {
		return id;
	}

	public String getInheritsFrom() {
		return inheritsFrom;
	}

	public List<LibraryJson> getLibraries() {
		return libraries;
	}
}
