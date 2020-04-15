package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.world.biome.BiomeList;
import amidst.mojangapi.world.biome.UnknownBiomeNameException;
import amidst.mojangapi.world.filter.WorldFilter_Biome;

@Immutable
public class WorldFilterJson_Biome {
	private volatile long distance;
	private volatile List<String> biomes = Collections.emptyList();
	
	private final BiomeList biomeList;

	@GsonConstructor
	public WorldFilterJson_Biome(BiomeList biomeList) {
		this.biomeList = biomeList;
	}

	public void validate(List<String> notifications) {
		if (biomes.isEmpty()) {
			notifications.add("No biomes for filter");
		} else {
			for (String name : biomes) {
				if (!biomeList.doesNameExist(name)) {
					notifications.add("invalid biome name: '" + name + "'");
				}
			}
		}
	}

	public WorldFilter_Biome createBiomeFilter() {
		return new WorldFilter_Biome(distance, createValidBiomeIndexes());
	}

	private Set<Short> createValidBiomeIndexes() {
		Set<Short> result = new HashSet<>();
		for (String name : biomes) {
			try {
				result.add((short) biomeList.getBiomeFromName(name).getId());
			} catch (UnknownBiomeNameException e) {
				AmidstLogger.error(e);
				AmidstMessageBox.displayError("Error", e);
			}
		}
		return result;
	}
}
