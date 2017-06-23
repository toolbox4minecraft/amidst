package amidst.filter;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Region;


@Immutable
public class BiomeConstraint implements Constraint {
	
	private final Region region;
	private final Biome biome;
	private final boolean checkDistance;
	
	public BiomeConstraint(Region region, Biome biome, boolean check) {
		this.region = region;
		this.biome = biome;
		checkDistance = check;
	}
	
	@Override
	public Region getRegion() {
		return region;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof BiomeConstraint))
			return false;
		
		BiomeConstraint o = (BiomeConstraint) other;
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
