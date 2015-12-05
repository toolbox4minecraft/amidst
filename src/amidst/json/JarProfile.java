package amidst.json;

import java.util.ArrayList;
import java.util.List;

public class JarProfile {
	private String id;
	private String time;
	private String releaseTime;
	private String type;
	private String minecraftArguments;
	private List<JarLibrary> libraries = new ArrayList<JarLibrary>();

	public JarProfile() {
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

	public List<JarLibrary> getLibraries() {
		return libraries;
	}
}
