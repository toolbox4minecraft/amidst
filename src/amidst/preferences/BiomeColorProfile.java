package amidst.preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonSyntaxException;

import amidst.Options;
import amidst.Util;
import amidst.logging.Log;
import amidst.minecraft.Biome;

public class BiomeColorProfile {
	private class BiomeColor {
		public String alias;
		public int r = 0;
		public int g = 0;
		public int b = 0;
		public BiomeColor(int rgb) {
			r = (rgb >> 16) & 0xFF;
			g = (rgb >> 8) & 0xFF;
			b = (rgb) & 0xFF;
		}
		public int toColorInt() {
			return Util.makeColor(r, g, b);
		}
	}
	public static boolean isEnabled = false;
	
	public HashMap<String, BiomeColor> colorMap = new HashMap<String, BiomeColor>(); 
	public int colorArray[] = new int[Biome.biomes.length];
	public String[] nameArray = new String[Biome.biomes.length];
	public String name;
	public String shortcut;
	
	public BiomeColorProfile() {
		name = "default";
		for (int i = 0; i < Biome.biomes.length; i++) {
			if (Biome.biomes[i] != null) {
				colorMap.put(Biome.biomes[i].name, new BiomeColor(Biome.biomes[i].color));
			}
		}
	}
	
	public void fillColorArray() {
		for (Map.Entry<String, BiomeColor> pairs : colorMap.entrySet()) {
			int index = Biome.indexFromName(pairs.getKey());
			if (index != -1) {
				colorArray[index] = pairs.getValue().toColorInt();
				nameArray[index] = (pairs.getValue().alias != null)?pairs.getValue().alias:Biome.biomes[index].name;
			} else {
				Log.i("Failed to find biome for: " + pairs.getKey() + " in profile: " + name);
			}
		}
	}
	
	public boolean save(File path) {
		String output = "";
		output += "{ \"name\":\"" + name + "\", \"colorMap\":[\r\n";
		
		for (Map.Entry<String, BiomeColor> pairs : colorMap.entrySet()) {
			output += "[ \"" + pairs.getKey() + "\", { ";
			output += "\"r\":" + pairs.getValue().r + ", "; 
			output += "\"g\":" + pairs.getValue().g + ", ";
			output += "\"b\":" + pairs.getValue().b + " } ],\r\n"; 
		}
		output = output.substring(0, output.length() - 3);
		
		output += " ] }\r\n";
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			writer.write(output);
			writer.close();
			return true;
		} catch ( IOException e) {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return false;
	}
	
	public void activate() {
		Options.instance.biomeColorProfile = this;
		Log.i("Biome color profile activated.");
		for (int i = 0; i < Biome.biomes.length; i++) {
			if (Biome.biomes[i] != null) {
				Biome.biomes[i].color = colorArray[i];
			}
		}
		if (amidst.map.Map.instance != null)
			amidst.map.Map.instance.resetFragments();
	}
	
	
	public static void scan() {
		Log.i("Searching for biome color profiles.");
		File colorProfileFolder = new File("./biome");
		
		if (!colorProfileFolder.exists() || !colorProfileFolder.isDirectory()) {
			Log.i("Unable to find biome color profile folder.");
			return;
		}
		
		File defaultProfileFile = new File("./biome/default.json");
		if (!defaultProfileFile.exists())
			if (!Options.instance.biomeColorProfile.save(defaultProfileFile))
				Log.i("Attempted to save default biome color profile, but encountered an error.");
		
		/*
		File[] colorProfiles = colorProfileFolder.listFiles();
		for (int i = 0; i < colorProfiles.length; i++) {
			if (colorProfiles[i].exists() && colorProfiles[i].isFile()) {
				try {
					BiomeColorProfile profile = Util.readObject(colorProfiles[i], BiomeColorProfile.class);
					profile.fillColorArray();
					profiles.add(profile);
				} catch (FileNotFoundException e) {
					Log.i("Unable to load file: " + colorProfiles[i]);
				}
			}
		}*/
		isEnabled = true;
	}
	
	public static BiomeColorProfile createFromFile(File file) {
		BiomeColorProfile profile = null;
		if (file.exists() && file.isFile()) {
			try {
				profile = Util.readObject(file, BiomeColorProfile.class);
				profile.fillColorArray();
			} catch (JsonSyntaxException e) {
				Log.w("Unable to load file: " + file);
				e.printStackTrace();
			} catch (IOException e) {
				Log.i("Unable to load file: " + file);
			}
		}
		return profile;
	}

	public String getAliasForId(int id) {
		if (nameArray[id] != null)
			return nameArray[id];
		return Biome.biomes[id].name;
	}
}
