package amidst.version;

import amidst.logging.Log;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;
import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;

public class VersionFactory {
	private MinecraftProfile[] profiles;

	public void scanForProfiles(LatestVersionList latestVersionList) {
		Log.i("Scanning for profiles.");
		LauncherProfilesJson launcherProfile = null;
		try {
			launcherProfile = LocalMinecraftInstallation
					.getDotMinecraftDirectory().readLauncherProfilesJson();
		} catch (Exception e) {
			Log.crash(e, "Error reading launcher_profiles.json");
			return;
		}
		Log.i("Successfully loaded profile list.");
		profiles = new MinecraftProfile[launcherProfile.getProfiles().size()];
		int i = 0;
		for (LaucherProfileJson installInformation : launcherProfile
				.getProfiles()) {
			profiles[i++] = new MinecraftProfile(installInformation,
					latestVersionList);
		}
	}

	public MinecraftProfile[] getProfiles() {
		return profiles;
	}
}
