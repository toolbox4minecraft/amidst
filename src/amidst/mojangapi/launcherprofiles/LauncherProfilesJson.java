package amidst.mojangapi.launcherprofiles;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class LauncherProfilesJson {
	private Map<String, LaucherProfileJson> profiles = Collections.emptyMap();

	public LauncherProfilesJson() {
		// no-argument constructor for gson
	}

	public LaucherProfileJson getProfile(String name) {
		return profiles.get(name);
	}

	public Collection<LaucherProfileJson> getProfiles() {
		return profiles.values();
	}
}
