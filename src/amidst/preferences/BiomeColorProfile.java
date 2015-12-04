package amidst.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

	public static void scan() {
		Log.i("Searching for biome color profiles.");
		if (!PROFILE_DIRECTORY.exists() || !PROFILE_DIRECTORY.isDirectory()) {
			Log.i("Unable to find biome color profile folder.");
			isEnabled = false;
		} else {
			saveDefaultProfileIfNecessary();
			isEnabled = true;
		}
	}

	private static void saveDefaultProfileIfNecessary() {
		File defaultProfileFile = new File(PROFILE_DIRECTORY, "default.json");
		if (!defaultProfileFile.exists()) {
			if (!Options.instance.biomeColorProfile.save(defaultProfileFile)) {
				Log.i("Attempted to save default biome color profile, but encountered an error.");
			}
		}
	}

	public static void visitProfiles(BiomeColorProfileVisitor visitor) {
		visitProfiles(PROFILE_DIRECTORY, visitor);
	}

	private static void visitProfiles(File directory,
			BiomeColorProfileVisitor visitor) {
		boolean entered = false;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				BiomeColorProfile profile = createFromFile(file);
				if (profile != null) {
					if (!entered) {
						entered = true;
						visitor.enterFolder(directory.getName());
					}
					visitor.visitProfile(profile);
				}
			} else {
				visitProfiles(file, visitor);
			}
		}
		if (entered) {
			visitor.leaveFolder();
		}
	}

	private static BiomeColorProfile createFromFile(File file) {
		BiomeColorProfile profile = null;
		if (file.exists() && file.isFile()) {
			try {
				profile = readProfile(file);
				profile.validate();
			} catch (JsonSyntaxException e) {
				Log.w("Unable to load file: " + file);
				e.printStackTrace();
			} catch (IOException e) {
				Log.i("Unable to load file: " + file);
			}
		}
		return profile;
	}

	private static BiomeColorProfile readProfile(File file)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BiomeColorProfile result = GSON.fromJson(reader,
				BiomeColorProfile.class);
		reader.close();
		return result;
	}

	public static boolean isEnabled() {
		return isEnabled;
	}

	private static final File PROFILE_DIRECTORY = new File("./biome");
	private static final Gson GSON = new Gson();

	private static boolean isEnabled = false;

	private final String name;
	private final String shortcut;
	private final Map<String, BiomeColor> colorMap = new HashMap<String, BiomeColor>();

	/**
	 * Do not initialize the instance variables directly to allow gson to set
	 * them.
	 */
	public BiomeColorProfile() {
		this.name = "default";
		this.shortcut = null;
		for (Biome biome : Biome.iterator()) {
			colorMap.put(biome.getName(), new BiomeColor(biome.getColor()));
		}
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

	public void activate() {
		Options.instance.biomeColorProfile = this;
		Log.i("Biome color profile activated.");
		for (Biome biome : Biome.iterator()) {
			biome.setColor(colorMap.get(biome.getName()).toColorInt());
		}
	}

	public boolean save(File file) {
		return writeToFile(file, serialize());
	}

	private String serialize() {
		String output = "{ \"name\":\"" + name + "\", \"colorMap\":[\r\n";
		output += serializeColorMap();
		return output + " ] }\r\n";
	}

	private String serializeColorMap() {
		String output = "";
		for (Map.Entry<String, BiomeColor> pairs : colorMap.entrySet()) {
			output += "[ \"" + pairs.getKey() + "\", { ";
			output += "\"r\":" + pairs.getValue().r + ", ";
			output += "\"g\":" + pairs.getValue().g + ", ";
			output += "\"b\":" + pairs.getValue().b + " } ],\r\n";
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
