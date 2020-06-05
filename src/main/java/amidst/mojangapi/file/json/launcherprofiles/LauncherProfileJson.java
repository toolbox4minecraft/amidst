package amidst.mojangapi.file.json.launcherprofiles;

import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.ReleaseType;

@Immutable
public class LauncherProfileJson {
	
	/**
	 * Some Minecraft installations using the legacy launcher have a profile
	 * with the key "(Default)" and no properties in the actual profile object.
	 * 
	 * The JSON looks like this:
	 * "(Default)": {},
	 * 
	 * This profile has the name null. Also, it cannot be deleted from the
	 * launcher. I guess this is a bug in the minecraft launcher. However, the
	 * minecraft launcher displays it with an empty string as name and it uses
	 * the latest stable release for it, so we do the same.
	 */	
	private volatile String name = "";
	private volatile String lastVersionId;
	private volatile String gameDir;
	private volatile String icon;
	private volatile List<ReleaseType> allowedReleaseTypes = ProfileType.LATEST_RELEASE.getAllowedReleaseTypes().get();
	
	private volatile ProfileType type = ProfileType.LEGACY;

	@GsonConstructor
	public LauncherProfileJson() {
	}

	public String getName() {
		return type.getDefaultName().orElse(name);
	}

	public String getLastVersionId() {
		return lastVersionId;
	}

	public String getGameDir() {
		return gameDir;
	}
	
	public String getIcon() {
		return icon;
	}

	public List<ReleaseType> getAllowedReleaseTypes() {
		return type.getAllowedReleaseTypes().orElse(allowedReleaseTypes);
	}
}
