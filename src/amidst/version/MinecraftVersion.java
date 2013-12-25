package amidst.version;

import java.io.File;

import amidst.logging.Log;

public class MinecraftVersion {
	private MinecraftVersion(String name) {
		
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
		
		//MinecraftVersion version = new MinecraftVersion(path.getName(), );
		
		
		return null;
	}
}
