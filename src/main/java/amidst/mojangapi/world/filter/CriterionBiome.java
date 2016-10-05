package amidst.mojangapi.world.filter;

import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Region;

import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Immutable
public class CriterionBiome implements Criterion {
	
	
	private final Region region;
	private final String name;
	private final List<Biome> biomes;
	private final boolean checkDistance;
	
	public CriterionBiome(String name, Region region, Collection<Biome> biomes, boolean check) {
		this.name = name;
		this.region = region;
		this.biomes = new ArrayList<>(biomes);
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
