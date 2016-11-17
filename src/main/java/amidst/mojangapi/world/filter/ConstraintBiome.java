package amidst.mojangapi.world.filter;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Region;


@Immutable
public class ConstraintBiome implements Constraint {
		
	private final Region region;
	private final Biome biome;
	private final boolean checkDistance;
	
	public ConstraintBiome(Region region, Biome biome, boolean check) {
		this.region = region;
		this.biome = biome;
		checkDistance = check;
	}
	
	@Override
	public Region getBiomeRegion() {
		return region;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof ConstraintBiome))
			return false;
		
		ConstraintBiome o = (ConstraintBiome) other;
		return region.equals(o.region)
			&& biome.equals(o.biome)
			&& checkDistance == o.checkDistance;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int h = region.hashCode();
		h = prime*h + biome.hashCode();
		h = prime*h + (checkDistance?0:1);
		return h;
	}
}
