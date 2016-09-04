package amidst.mojangapi.file.world.filter;

import java.util.Set;

import amidst.mojangapi.world.coordinates.Region;

public interface Criterion {
	public String getName();
	
	public Set<Region> getBiomeRegionsNeeded();
	
	//TODO
}
