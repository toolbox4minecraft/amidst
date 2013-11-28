package amidst.preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import amidst.Log;
import amidst.Options;
import amidst.Util;
import amidst.json.LauncherProfile;
import amidst.minecraft.Biome;

public class BiomeColorProfile {
	private class BiomeColor {
		public int r = 0;
		public int g = 0;
		public int b = 0;
		public BiomeColor(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
		public BiomeColor(int rgb) {
			r = (rgb >> 16) & 0xFF;
			g = (rgb >> 8) & 0xFF;
			b = (rgb) & 0xFF;
		}
		public int toColorInt() {
			return Util.makeColor(r, g, b);
		}
	};
	public static boolean isEnabled = false;
	public static ArrayList<BiomeColorProfile> profiles = new ArrayList<BiomeColorProfile>();
	
	public HashMap<String, BiomeColor> colorMap = new HashMap<String, BiomeColor>(); 
	public int colorArray[] = new int[Biome.length << 1];
	public String name;
	
	public BiomeColorProfile() {
		name = "default";
		for (int i = 0; i < Biome.length; i++) {
			colorMap.put(Biome.biomes[i].name, new BiomeColor(Biome.biomes[i].color));
			colorMap.put(Biome.biomes[i+128].name, new BiomeColor(Biome.biomes[i+128].color));
		}
	}
	
	public void fillColorArray() {
		for (Map.Entry<String, BiomeColor> pairs : colorMap.entrySet()) {
			int index = Biome.indexFromName(pairs.getKey());
			if (index >= 128)
				index = index - 128 + Biome.length;
			if (index != -1)
				colorArray[index] = pairs.getValue().toColorInt();
			else
				Log.i("Failed to find biome for: " + pairs.getKey() + " in profile: " + name);
		}
	}
	
	public boolean save(File path) {
		String output = "";
		output += "{\r\n";
		output += "\t\"name\":\"" + name +"\",\r\n";
		output += "\t\"colorMap\":[\r\n";
		
		for (Map.Entry<String, BiomeColor> pairs : colorMap.entrySet()) {
			output += "\t\t[\r\n";
			output += "\t\t\t\"" + pairs.getKey() +"\",\r\n";
			output += "\t\t\t{\r\n";
			output += "\t\t\t\t\"r\":" + pairs.getValue().r + ",\r\n"; 
			output += "\t\t\t\t\"g\":" + pairs.getValue().g + ",\r\n";
			output += "\t\t\t\t\"b\":" + pairs.getValue().b + "\r\n"; 
			output += "\t\t\t}\r\n";
			output += "\t\t],\r\n";
		}
		output = output.substring(0, output.length() - 3);
		output += "\r\n";
		
		output += "\t]\r\n";
		output += "}\r\n";
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
		for (int i = 0; i < Biome.length; i++) {
			Biome.biomes[i].color = colorArray[i];
			Biome.biomes[i+128].color = colorArray[i + Biome.length];
		}
		if (amidst.map.Map.instance != null)
			amidst.map.Map.instance.resetFragments();
	}
	
	
	public static void scanAndLoad() {
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
		}
		isEnabled = true;
	}
}
