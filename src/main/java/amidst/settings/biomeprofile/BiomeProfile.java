package amidst.settings.biomeprofile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class BiomeProfile {
	private static BiomeProfile createDefaultProfile() {
		final String profileFile = "/amidst/mojangapi/default_biome_profile.json";
		try (InputStream stream = BiomeProfile.class.getResourceAsStream(profileFile)) {
			return JsonReader.readString(CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8)), BiomeProfile.class);
		} catch (IOException | FormatException e) {
			throw new RuntimeException("Unable to create default biome profile", e);
		}
	}

	public static BiomeProfile getDefaultProfile() {
		return DEFAULT_PROFILE;
	}

	private static final BiomeProfile DEFAULT_PROFILE = createDefaultProfile();

	private volatile String name;
	private volatile String shortcut;
	private volatile Map<Integer, BiomeColorJson> colorMap;

	@GsonConstructor
	public BiomeProfile() {
	}

	public String getName() {
		return name == null ? "<unnamed>" : name;
	}

	public String getShortcut() {
		return shortcut;
	}

	public boolean validate() {
		if(this.colorMap == null) {
			AmidstLogger.info("Color map is missing in profile: {}", name);
			return false;
		}

		if(this.name == null) {
			AmidstLogger.info("Name is missing in profile");
			return false;
		}
		
		return true;
	}

	public ConcurrentHashMap<Integer, BiomeColor> createBiomeColorMap() {
		ConcurrentHashMap<Integer, BiomeColor> result = new ConcurrentHashMap<Integer, BiomeColor>();
		colorMap.forEach((k,v) -> result.put(k, v.createBiomeColor()));
		return result;
	}

	public boolean save(Path file) {
		return writeToFile(file, serialize());
	}

	private String serialize() {
		String output = "{ \"name\":\"" + name + "\", \"colorMap\":[\r\n";
		output += serializeColorMap();
		return output + " ] }\r\n";
	}

	/**
	 * This method uses the sorted color map, so the serialization will have a
	 * reproducible order.
	 */
	private String serializeColorMap() {
		String output = "";
		for (Map.Entry<Integer, BiomeColorJson> pairs : getSortedColorMapEntries()) {
			output += "[ \"" + pairs.getKey() + "\", { ";
			output += "\"r\":" + pairs.getValue().getR() + ", ";
			output += "\"g\":" + pairs.getValue().getG() + ", ";
			output += "\"b\":" + pairs.getValue().getB() + " } ],\r\n";
		}
		return output.substring(0, output.length() - 3);
	}

	private Set<Entry<Integer, BiomeColorJson>> getSortedColorMapEntries() {
		if(colorMap == null) {
			return Collections.emptySet();
		}

		SortedMap<Integer, BiomeColorJson> result = new TreeMap<>(Integer::compare);
		result.putAll(colorMap);
		return result.entrySet();
	}

	private boolean writeToFile(Path file, String output) {
		try (BufferedWriter writer = Files.newBufferedWriter(file)) {
			writer.write(output);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
