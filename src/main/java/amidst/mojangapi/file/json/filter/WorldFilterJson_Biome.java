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

	public WorldFilter_Biome createBiomeFilter() {
		if (biomes.size() == 0) {
			throw new IllegalStateException("No biomes for filter");
		}
		return new WorldFilter_Biome(distance, biomes);
	}
}
