package amidst.settings.biomeprofile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
class BiomeProfileOld {
	private volatile String name;
	private volatile String shortcut;
	private volatile Map<String, BiomeColorJson> colorMap;

	@GsonConstructor
	BiomeProfileOld() {
	}

	public BiomeProfile convertToNewFormat() {
		final String conversionFile = "/amidst/mojangapi/conversion_map.txt";
		Map<String, Integer> conversionMap = new HashMap<String, Integer>();
		try (InputStream stream = BiomeProfile.class.getResourceAsStream(conversionFile)) {
			try (Scanner scanner = new Scanner(stream)) {
				while(scanner.hasNextLine()) {
					String[] parts = scanner.nextLine().split(",");
					if (parts.length != 2) {
						throw new RuntimeException("Invalid biome profile conversion file");
					} else {
						try {
							conversionMap.put(parts[0], Integer.parseInt(parts[1]));
						} catch (NumberFormatException e) {
							throw new RuntimeException("Invalid biome profile conversion file", e);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to get biome profile conversion file", e);
		}
		
		SortedMap<Integer, BiomeColorJson> newMap = new TreeMap<Integer, BiomeColorJson>(Integer::compare);
		for(Entry<String, BiomeColorJson> entry : colorMap.entrySet()) {
			Integer id = conversionMap.get(entry.getKey());
			if(id == null) {
				throw new RuntimeException("Unable to convert biome profile: Biome " + entry.getKey() + " not found");
			}
			newMap.put(id, entry.getValue());
		}
		
		return new BiomeProfile(name, shortcut, newMap);
	}
}
