package amidst.mojangapi.launcherprofiles;

import java.util.Collection;
import java.util.Map;

public class LauncherProfiles {
	private Map<String, LaucherProfile> profiles;

	public LauncherProfiles() {
		// no-argument constructor for gson
	}

	public LaucherProfile getProfile(String name) {
		return profiles.get(name);
	}

	public Collection<LaucherProfile> getProfiles() {
		return profiles.values();
	}
}
