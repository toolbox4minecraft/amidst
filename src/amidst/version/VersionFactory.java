package amidst.version;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import amidst.logging.Log;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.mojangapi.MojangAPI;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;
import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;

import com.google.gson.JsonSyntaxException;

public class VersionFactory {
	private MinecraftVersion[] localVersions;
	private MinecraftProfile[] profiles;

	// TODO: call me?
	public void scanForLocalVersions() {
		File versionPath = new File(
				LocalMinecraftInstallation.getMinecraftDirectory()
						+ "/versions");
		if (!versionPath.exists()) {
			Log.e("Cannot find version directory.");
			return;
		} else if (!versionPath.isDirectory()) {
			Log.e("Attempted to open version directory but found file.");
			return;
		}
		File[] versionDirectories = versionPath.listFiles();
		Stack<MinecraftVersion> versionStack = new Stack<MinecraftVersion>();
		for (int i = 0; i < versionDirectories.length; i++) {
			MinecraftVersion version = MinecraftVersion
					.fromVersionPath(versionDirectories[i]);
			if (version != null) {
				versionStack.add(version);
			}
		}
		if (versionStack.size() == 0) {
			return;
		}
		localVersions = new MinecraftVersion[versionStack.size()];
		versionStack.toArray(localVersions);
	}

	public void scanForProfiles(LatestVersionList latestVersionList) {
		Log.i("Scanning for profiles.");
		File profileJsonFile = new File(
				LocalMinecraftInstallation.getMinecraftDirectory()
						+ "/launcher_profiles.json");
		LauncherProfilesJson launcherProfile = null;
		try {
			launcherProfile = MojangAPI.launcherProfilesFrom(profileJsonFile);
		} catch (JsonSyntaxException e) {
			Log.crash(e, "Syntax exception thrown from launch_profiles.json");
			return;
		} catch (IOException e) {
			Log.crash(e, "Unable to open launch_profiles.json");
			return;
		}
		Log.i("Successfully loaded profile list.");
		profiles = new MinecraftProfile[launcherProfile.getProfiles().size()];
		int i = 0;
		for (LaucherProfileJson installInformation : launcherProfile.getProfiles()) {
			profiles[i++] = new MinecraftProfile(installInformation,
					latestVersionList);
		}
	}

	public MinecraftProfile[] getProfiles() {
		return profiles;
	}

	public MinecraftVersion[] getLocalVersions() {
		return localVersions;
	}
}
