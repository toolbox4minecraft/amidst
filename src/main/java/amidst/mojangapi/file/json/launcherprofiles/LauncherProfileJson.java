package amidst.mojangapi.file.json.launcherprofiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

@Immutable
public class LauncherProfileJson {
	/**
	 * Some Minecraft installations have a profile with the key "(Default)" and
	 * no properties in the actual profile object. The JSON looks like this:
	 * 
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
	private volatile List<ReleaseType> allowedReleaseTypes = Arrays.asList(ReleaseType.RELEASE);

	@GsonConstructor
	public LauncherProfileJson() {
	}

	public String getName() {
		return name;
	}

	public String getLastVersionId() {
		return lastVersionId;
	}

	public String getGameDir() {
		return gameDir;
	}

	@NotNull
	public ProfileDirectory createValidProfileDirectory(MojangApi mojangApi) throws FileNotFoundException {
		if (gameDir != null) {
			ProfileDirectory result = new ProfileDirectory(new File(gameDir));
			if (result.isValid()) {
				return result;
			} else {
				throw new FileNotFoundException("cannot find valid profile directory for launcher profile '" + name
						+ "': " + gameDir);
			}
		} else {
			return new ProfileDirectory(mojangApi.getDotMinecraftDirectory().getRoot());
		}
	}

	@NotNull
	public VersionDirectory createValidVersionDirectory(MojangApi mojangApi) throws FileNotFoundException {
		VersionListJson versionList = mojangApi.getVersionList();
		if (lastVersionId != null) {
			VersionDirectory result = mojangApi.createVersionDirectory(lastVersionId);
			if (result.isValid()) {
				return result;
			}
		} else {
			VersionDirectory result = versionList.tryFindFirstValidVersionDirectory(allowedReleaseTypes, mojangApi);
			if (result != null) {
				return result;
			}
		}
		throw new FileNotFoundException("cannot find valid version directory for launcher profile '" + name + "'");
	}
}
