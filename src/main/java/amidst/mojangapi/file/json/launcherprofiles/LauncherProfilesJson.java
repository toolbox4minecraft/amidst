package amidst.mojangapi.file.json.launcherprofiles;

import java.util.Collections;
import java.util.Map;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject(ignoreUnknown=true)
public class LauncherProfilesJson {
	private volatile Map<String, LauncherProfileJson> profiles = Collections.emptyMap();

	public LauncherProfilesJson() {
	}

	public Map<String, LauncherProfileJson> getProfiles() {
		return profiles;
	}
}
