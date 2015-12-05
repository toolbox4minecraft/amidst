package amidst.mojangapi.version;

import java.util.List;

public class Version {
	private String id;
	private String time;
	private String releaseTime;
	private String type;
	private String minecraftArguments;
	private List<Library> libraries;

	public Version() {
		// no-argument constructor for gson
	}

	public String getId() {
		return id;
	}

	public String getTime() {
		return time;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public String getType() {
		return type;
	}

	public String getMinecraftArguments() {
		return minecraftArguments;
	}

	public List<Library> getLibraries() {
		return libraries;
	}
}
