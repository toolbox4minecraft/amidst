package amidst.version;

import java.io.File;

import amidst.logging.Log;

public class MinecraftVersion {
	private File jarFile, jsonFile;
	private String name;
	private String shortName;
	
	private MinecraftVersion(String name, File jarFile, File jsonFile) {
		this.name = name;
		if (name.length() > 10)
			name = name.substring(0, 7) + "...";
		shortName = name;
		
		this.jarFile = jarFile;
		this.jsonFile = jsonFile;
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
	
	public String getName() {
		return name;
	}
	public String getShortName() {
		return shortName;
	}
}
