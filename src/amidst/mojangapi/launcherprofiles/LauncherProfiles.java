package amidst.mojangapi.launcherprofiles;

import java.util.Map;

public class LauncherProfiles {
	private Map<String, LaucherProfile> profiles;
	private String selectedProfile;
	private String clientToken;

	public LauncherProfiles() {
		// no-argument constructor for gson
	}

	public Map<String, LaucherProfile> getProfiles() {
		return profiles;
	}

	public String getSelectedProfile() {
		return selectedProfile;
	}

	public String getClientToken() {
		return clientToken;
	}
}
