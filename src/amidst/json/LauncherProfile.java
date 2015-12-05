package amidst.json;

import java.util.HashMap;

public class LauncherProfile {
	private HashMap<String, InstallInformation> profiles;
	private String selectedProfile;
	private String clientToken;

	public HashMap<String, InstallInformation> getProfiles() {
		return profiles;
	}

	public String getSelectedProfile() {
		return selectedProfile;
	}

	public String getClientToken() {
		return clientToken;
	}
}
