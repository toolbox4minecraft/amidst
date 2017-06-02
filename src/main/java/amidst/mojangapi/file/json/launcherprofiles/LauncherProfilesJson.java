package amidst.mojangapi.file.json.launcherprofiles;

import java.util.Collections;
import java.util.Map;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class LauncherProfilesJson {
	private volatile Map<String, LauncherProfileJson> profiles = Collections.emptyMap();

	@GsonConstructor
	public LauncherProfilesJson() {
	}

	public Map<String, LauncherProfileJson> getProfiles() {
		return profiles;
	}
}
