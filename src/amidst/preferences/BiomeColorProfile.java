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

		isEnabled = true;
	}

	public static void visitProfiles(BiomeColorProfileVisitor visitor) {
		visitProfiles(new File("./biome"), visitor);
	}

	private static void visitProfiles(File folder,
			BiomeColorProfileVisitor visitor) {
		boolean entered = false;
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				BiomeColorProfile profile = createFromFile(file);
				if (profile != null) {
					if (!entered) {
						entered = true;
						visitor.enterFolder(folder.getName());
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

	private static final Gson GSON = new Gson();

	private static boolean isEnabled = false;

	private String name;
	private String shortcut;
	private Map<String, BiomeColor> colorMap = new HashMap<String, BiomeColor>();
	private int[] colorArray = new int[Biome.getBiomesLength()];

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

	public String getName() {
		return name;
	}

	public String getShortcut() {
		return shortcut;
	}
}
