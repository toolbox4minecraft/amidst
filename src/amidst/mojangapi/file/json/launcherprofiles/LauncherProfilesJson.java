package amidst.mojangapi.file.json.launcherprofiles;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import amidst.documentation.GsonConstructor;

public class LauncherProfilesJson {
	private Map<String, LauncherProfileJson> profiles = Collections.emptyMap();

	@GsonConstructor
	public LauncherProfilesJson() {
	}

	public LauncherProfileJson getProfile(String name) {
		return profiles.get(name);
	}

	public Collection<LauncherProfileJson> getProfiles() {
		return profiles.values();
	}
}
