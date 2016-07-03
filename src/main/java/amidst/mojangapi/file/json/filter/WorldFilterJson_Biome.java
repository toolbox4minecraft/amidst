package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
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
		}
	}

	public WorldFilter_Biome createBiomeFilter() {
		return new WorldFilter_Biome(distance, biomes);
	}
}
