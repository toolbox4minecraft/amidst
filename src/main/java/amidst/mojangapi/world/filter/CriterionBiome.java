package amidst.mojangapi.world.filter;

import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Region;

import java.util.Set;
import java.util.Collections;

@Immutable
public class CriterionBiome implements Criterion {
	
	
	private Region region;
	private String name;
	private List<Biome> biomes;
	private boolean checkDistance;
	
	public CriterionBiome(String name, Region region, List<Biome> biomes, boolean check) {
		this.name = name;
		this.region = region;
		this.biomes = biomes;
		checkDistance = check;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<Region> getBiomeRegionsNeeded() {
		return Collections.singleton(region);
	}
}
