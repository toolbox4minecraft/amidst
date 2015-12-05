package amidst.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import amidst.logging.Log;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.mojangapi.launcherprofiles.LaucherProfile;
import amidst.mojangapi.launcherprofiles.LauncherProfiles;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class VersionFactory {
	private static final Gson GSON = new Gson();

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
		LauncherProfiles launcherProfile = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					profileJsonFile));
			launcherProfile = GSON.fromJson(reader, LauncherProfiles.class);
			reader.close();
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
		for (LaucherProfile installInformation : launcherProfile.getProfiles()
				.values()) {
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
