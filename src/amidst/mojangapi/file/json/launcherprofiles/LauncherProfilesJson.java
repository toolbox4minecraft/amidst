package amidst.mojangapi.file.json.launcherprofiles;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class LauncherProfilesJson {
	private Map<String, LauncherProfileJson> profiles = Collections.emptyMap();

	public LauncherProfilesJson() {
		// no-argument constructor for gson
	}

	public LauncherProfileJson getProfile(String name) {
		return profiles.get(name);
	}

	public Collection<LauncherProfileJson> getProfiles() {
		return profiles.values();
	}
}
