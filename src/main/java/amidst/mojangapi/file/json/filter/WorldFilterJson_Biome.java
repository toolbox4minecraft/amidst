package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.filter.WorldFilter_Biome;

@Immutable
public class WorldFilterJson_Biome {
	private volatile long distance;
	private volatile List<String> biomes = Collections.emptyList();

	@GsonConstructor
	public WorldFilterJson_Biome() {
	}

	public void validate(List<String> notifications) {
		if (biomes.isEmpty()) {
			notifications.add("No biomes for filter");
		} else {
			for (String name : biomes) {
				if (!Biome.exists(name)) {
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
			result.add((short) Biome.getByName(name).getIndex());
		}
		return result;
	}
}
