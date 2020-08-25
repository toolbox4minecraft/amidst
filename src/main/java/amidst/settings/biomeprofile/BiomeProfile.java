package amidst.settings.biomeprofile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class BiomeProfile implements Serializable {
	private static final long serialVersionUID = 656038328314515511L;

	private static BiomeProfile createDefaultProfile() {
		final String profileFile = "/amidst/mojangapi/default_biome_profile.json";
		try (InputStream stream = BiomeProfile.class.getResourceAsStream(profileFile)) { // For some reason this is the only way we can read the file from inside and outside the jar
			try (Scanner scanner = new Scanner(stream)) {
				StringBuffer buffer = new StringBuffer();
				while(scanner.hasNext()){
					buffer.append(scanner.nextLine());
				}
				return JsonReader.readString(buffer.toString(), BiomeProfile.class);
			}
		} catch (IOException | FormatException e) {
			throw new RuntimeException("Unable to create default biome profile", e);
		}
	}

	public static BiomeProfile getDefaultProfile() {
		return DEFAULT_PROFILE;
	}
	
	public static BiomeProfile createExampleProfile() {
		return new BiomeProfile("example", "control E", DEFAULT_PROFILE.colorMap);
	}

	private static final BiomeProfile DEFAULT_PROFILE = createDefaultProfile();

	private volatile String name;
	private volatile String shortcut;
	private volatile Map<Integer, BiomeColorJson> colorMap;

	@GsonConstructor
	public BiomeProfile() {
	}
	
	BiomeProfile(String name, String shortcut, Map<Integer, BiomeColorJson> colorMap) {
		this.name = name;
		this.shortcut = shortcut;
		this.colorMap = colorMap;
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

	public String serialize() {
		String output = "{ \"name\":\"" + name + "\", ";
		output += shortcut != null ? "\"shortcut\":\"" + shortcut + "\", " : "";
		output += "\"colorMap\":[\r\n" + serializeColorMap();
		return output + " ] }\r\n";
	}

	/**
	 * This method uses the sorted color map, so the serialization will have a
	 * reproducible order.
	 */
	private String serializeColorMap() {
		String output = "";
		for (Map.Entry<Integer, BiomeColorJson> pairs : getSortedColorMapEntries()) {
			output += "[ " + pairs.getKey() + ", { ";
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
