package amidst.json;

import java.util.Map;

public class LauncherProfile {
	private Map<String, InstallInformation> profiles;
	private String selectedProfile;
	private String clientToken;

	public LauncherProfile() {
		// no-argument constructor for gson
	}

	public Map<String, InstallInformation> getProfiles() {
		return profiles;
	}

	public String getSelectedProfile() {
		return selectedProfile;
	}

	public String getClientToken() {
		return clientToken;
	}
}
