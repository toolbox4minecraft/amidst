package amidst.settings.biomecolorprofile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeColor;

import com.google.gson.GsonBuilder;

@Immutable
public class BiomeColorProfile {
	public static void saveDefaultProfileIfNecessary() {
		if (!isEnabled()) {
			Log.i("Unable to find biome color profile directory.");
		} else {
			Log.i("Found biome color profile directory.");
			if (DEFAULT_PROFILE_FILE.isFile()) {
				Log.i("Found default biome color profile.");
			} else if (DEFAULT_PROFILE.save(DEFAULT_PROFILE_FILE)) {
				Log.i("Saved default biome color profile.");
			} else {
				Log.i("Attempted to save default biome color profile, but encountered an error.");
			}
		}
	}

	private static Map<String, BiomeColorJson> createDefaultColorMap() {
		Map<String, BiomeColorJson> result = new HashMap<String, BiomeColorJson>();
		for (Biome biome : Biome.allBiomes()) {
			result.put(biome.getName(), biome.getDefaultColor()
					.createBiomeColorJson());
		}
		return result;
	}

	public static BiomeColorProfile getDefaultProfile() {
		return DEFAULT_PROFILE;
	}

	public static boolean isEnabled() {
		return PROFILE_DIRECTORY.isDirectory();
	}

	private static final BiomeColorProfile DEFAULT_PROFILE = new BiomeColorProfile(
			"default", null, createDefaultColorMap());
	public static final File PROFILE_DIRECTORY = new File("./biome");
	public static final File DEFAULT_PROFILE_FILE = new File(PROFILE_DIRECTORY,
			"default.json");

	private String name;
	private String shortcut;
	private Map<String, BiomeColorJson> colorMap;

	@GsonConstructor
	public BiomeColorProfile() {
	}

	private BiomeColorProfile(String name, String shortcut,
			Map<String, BiomeColorJson> colorMap) {
		this.name = name;
		this.shortcut = shortcut;
		this.colorMap = colorMap;
	}

	public String getName() {
		return name;
	}

	public String getShortcut() {
		return shortcut;
	}

	public void validate() {
		for (String biomeName : colorMap.keySet()) {
			if (!Biome.exists(biomeName)) {
				Log.i("Failed to find biome for: " + biomeName
						+ " in profile: " + name);
			}
		}
	}

	public BiomeColor[] createBiomeColorArray() {
		BiomeColor[] result = new BiomeColor[Biome.getBiomesLength()];
		for (Biome biome : Biome.allBiomes()) {
			result[biome.getIndex()] = getBiomeColor(biome);
		}
		return result;
	}

	private BiomeColor getBiomeColor(Biome biome) {
		if (colorMap.containsKey(biome.getName())) {
			return colorMap.get(biome.getName()).createBiomeColor();
		} else {
			return biome.getDefaultColor();
		}
	}

	public boolean save(File file) {
		return writeToFile(file, serialize());
	}

	private String serializeWithGson() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

	// TODO: @skiphs use serializeWithGson() instead?
	private String serialize() {
		String output = "{ \"name\":\"" + name + "\", \"colorMap\":[\r\n";
		output += serializeColorMap();
		return output + " ] }\r\n";
	}

	private String serializeColorMap() {
		String output = "";
		for (Map.Entry<String, BiomeColorJson> pairs : colorMap.entrySet()) {
			output += "[ \"" + pairs.getKey() + "\", { ";
			output += "\"r\":" + pairs.getValue().getR() + ", ";
			output += "\"g\":" + pairs.getValue().getG() + ", ";
			output += "\"b\":" + pairs.getValue().getB() + " } ],\r\n";
		}
		return output.substring(0, output.length() - 3);
	}

	private boolean writeToFile(File file, String output) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(output);
			writer.close();
			return true;
		} catch (IOException e) {
			closeWriter(writer);
		}
		return false;
	}

	private void closeWriter(BufferedWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
