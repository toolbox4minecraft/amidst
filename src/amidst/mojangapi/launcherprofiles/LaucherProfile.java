package amidst.mojangapi.launcherprofiles;

import amidst.mojangapi.version.Resolution;

public class LaucherProfile {
	private String name;
	private String lastVersionId = "latest";
	private String gameDir;
	private String javaDir;
	private String javaArgs;
	private String playerUUID;
	private Resolution resolution;
	private String[] allowedReleaseTypes = new String[] { "release" };

	public LaucherProfile() {
		// no-argument constructor for gson
	}

	public String getName() {
		return name;
	}

	public String getLastVersionId() {
		return lastVersionId;
	}

	public String getGameDir() {
		return gameDir;
	}

	public String getJavaDir() {
		return javaDir;
	}

	public String getJavaArgs() {
		return javaArgs;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public Resolution getResolution() {
		return resolution;
	}

	public String[] getAllowedReleaseTypes() {
		return allowedReleaseTypes;
	}
}