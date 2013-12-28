package amidst.version;

import java.io.File;
import java.util.HashMap;

import amidst.Util;
import amidst.logging.Log;

public class MinecraftVersion {
	private File jarFile, jsonFile;
	private String name;
	
	private MinecraftVersion(String name, File jarFile, File jsonFile) {
		this.name = name;
		
		this.jarFile = jarFile;
		this.jsonFile = jsonFile;
	}
	public static MinecraftVersion fromVersionId(String lastVersionId) {
		return fromVersionPath(new File(Util.minecraftDirectory + "/versions/" + lastVersionId));
	}
	public static MinecraftVersion fromVersionPath(File path) {
		File jarFile = new File(path + "/" + path.getName() + ".jar");
		File jsonFile = new File(path + "/" + path.getName() + ".json");
		
		if (!jarFile.exists() || jarFile.isDirectory()) {
			Log.w("Unable to load MinecraftVersion at path: " + path + " because jarFile: " + jarFile + " is missing or a directory.");
			return null;
		}
		if (!jsonFile.exists() || jsonFile.isDirectory()) {
			Log.w("Unable to load MinecraftVersion at path: " + path + " because jsonFile: " + jsonFile + " is missing or a directory.");
			return null;
		}
		
		MinecraftVersion version = new MinecraftVersion(path.getName(), jarFile, jsonFile);
		return version;
	}
	public static MinecraftVersion fromLatestRelease() {
		MinecraftVersion version = null;
		HashMap<String, String>[] versions = LatestVersionList.get().getVersions();
		
		for (int i = 0; i < versions.length; i++) {
			if (versions[i].get("type").equals("release") && (version = fromVersionId(versions[i].get("id"))) != null)
				return version;
		}
		return null;
	}
	
	public static MinecraftVersion fromLatestSnapshot() {
		MinecraftVersion version = null;
		HashMap<String, String>[] versions = LatestVersionList.get().getVersions();
		
		for (int i = 0; i < versions.length; i++) {
			if ((version = fromVersionId(versions[i].get("id"))) != null)
				return version;
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	public File getJarFile() {
		return jarFile;
	}
}
