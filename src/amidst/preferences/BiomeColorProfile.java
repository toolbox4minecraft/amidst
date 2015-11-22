package amidst.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import amidst.Options;
import amidst.logging.Log;
import amidst.minecraft.Biome;
import amidst.utilities.ColorUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class BiomeColorProfile {
	private class BiomeColor {
		private final int r;
		private final int g;
		private final int b;

		public BiomeColor(int rgb) {
			r = (rgb >> 16) & 0xFF;
			g = (rgb >> 8) & 0xFF;
			b = (rgb) & 0xFF;
		}

		public int toColorInt() {
			return ColorUtils.makeColor(r, g, b);
		}
	}

	private static final Gson GSON = new Gson();

	public static void scan() {
		Log.i("Searching for biome color profiles.");
		File colorProfileFolder = new File("./biome");

		if (!colorProfileFolder.exists() || !colorProfileFolder.isDirectory()) {
			Log.i("Unable to find biome color profile folder.");
			return;
		}

		File defaultProfileFile = new File("./biome/default.json");
		if (!defaultProfileFile.exists()) {
			if (!Options.instance.biomeColorProfile.save(defaultProfileFile)) {
				Log.i("Attempted to save default biome color profile, but encountered an error.");
			}
		}

		/*
		 * File[] colorProfiles = colorProfileFolder.listFiles(); for (int i =
		 * 0; i < colorProfiles.length; i++) { if (colorProfiles[i].exists() &&
		 * colorProfiles[i].isFile()) { try { BiomeColorProfile profile =
		 * Util.readObject(colorProfiles[i], BiomeColorProfile.class);
		 * profile.fillColorArray(); profiles.add(profile); } catch
		 * (FileNotFoundException e) { Log.i("Unable to load file: " +
		 * colorProfiles[i]); } } }
		 */
		isEnabled = true;
	}

	public static BiomeColorProfile createFromFile(File file) {
		BiomeColorProfile profile = null;
		if (file.exists() && file.isFile()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				BiomeColorProfile result = GSON.fromJson(reader,
						BiomeColorProfile.class);
				reader.close();
				profile = result;
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

	public static boolean isEnabled = false;

	public HashMap<String, BiomeColor> colorMap = new HashMap<String, BiomeColor>();
	public int colorArray[] = new int[Biome.getBiomesLength()];
	public String[] nameArray = new String[Biome.getBiomesLength()];
	public String name;
	public String shortcut;

	public BiomeColorProfile() {
		name = "default";
		for (Biome biome : Biome.iterator()) {
			colorMap.put(biome.getName(), new BiomeColor(biome.getColor()));
		}
	}

	public void fillColorArray() {
		for (Map.Entry<String, BiomeColor> pairs : colorMap.entrySet()) {
			Biome biome = Biome.getByName(pairs.getKey());
			if (biome != null) {
				int index = biome.getIndex();
				colorArray[index] = pairs.getValue().toColorInt();
				nameArray[index] = biome.getName();
			} else {
				Log.i("Failed to find biome for: " + pairs.getKey()
						+ " in profile: " + name);
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
		} catch (IOException e) {
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
		for (Biome biome : Biome.iterator()) {
			biome.setColor(colorArray[biome.getIndex()]);
		}
	}

	public String getAliasForId(int id) {
		if (nameArray[id] != null) {
			return nameArray[id];
		} else {
			return Biome.getByIndex(id).getName();
		}
	}
}
