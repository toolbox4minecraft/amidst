package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.filter.BiomeFilter;

@Immutable
public class BiomeFilterJson {
	private volatile long distance;
	private volatile List<String> biomes = Collections.emptyList();

	@GsonConstructor
	public BiomeFilterJson() {
	}

	public BiomeFilter createBiomeFilter() {
		if (biomes.size() == 0) {
			throw new IllegalStateException("No biomes for filter");
		}
		return new BiomeFilter(distance, biomes);
	}
}