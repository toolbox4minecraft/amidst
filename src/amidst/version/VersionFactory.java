package amidst.version;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import com.google.gson.JsonSyntaxException;

import amidst.Util;
import amidst.json.InstallInformation;
import amidst.json.LauncherProfile;
import amidst.logging.Log;

public class VersionFactory {
	private MinecraftVersion[] localVersions;
	private MinecraftProfile[] profiles;
	public VersionFactory() {
		
	}
	
	public void scanForLocalVersions() {
		File versionPath = new File(Util.minecraftDirectory + "/versions");
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
			MinecraftVersion version = MinecraftVersion.fromVersionPath(versionDirectories[i]);
			if (version != null)
				versionStack.add(version);
		}
		if (versionStack.size() == 0)
			return;
		
		localVersions = new MinecraftVersion[versionStack.size()];
		versionStack.toArray(localVersions);
	}
	
	public void scanForProfiles() {
		Log.i("Scanning for profiles.");
		File profileJsonFile = new File(Util.minecraftDirectory + "/launcher_profiles.json");
		LauncherProfile launcherProfile = null;
		try {
			launcherProfile = Util.readObject(profileJsonFile, LauncherProfile.class);
		} catch (JsonSyntaxException e) {
			Log.crash(e, "Syntax exception thrown from launch_profiles.json");
			return;
		} catch (IOException e) {
			Log.crash(e, "Unable to open launch_profiles.json");
			return;
		}
		Log.i("Successfully loaded profile list.");
	
		profiles = new MinecraftProfile[launcherProfile.profiles.size()];
		
		int i = 0;
		for (InstallInformation installInformation : launcherProfile.profiles.values())
			profiles[i++] = new MinecraftProfile(installInformation);
	
		
	}
	public MinecraftProfile[] getProfiles() {
		return profiles;
	}
	public MinecraftVersion[] getLocalVersions() {
		return localVersions;
	}
}
