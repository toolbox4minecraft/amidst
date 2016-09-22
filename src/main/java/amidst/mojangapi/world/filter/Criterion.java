package amidst.mojangapi.world.filter;

import java.util.Set;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.Region;

@Immutable
public interface Criterion {
	public String getName();
	
	public Set<Region> getBiomeRegionsNeeded();
	
	//TODO
}
