package amidst.filter;

import java.util.Map;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Resolution;

public class WorldFilter {
	
	public static final Resolution REGION_SIZE = Resolution.FRAGMENT;

	//if null, use world spawn point
	private final Coordinates globalCenter;
	
	private final Map<String, Criterion> criteria;
	private final Criterion match;
	
	public WorldFilter(Coordinates center, Map<String, Criterion> criteria, Criterion match) {
		globalCenter = center;
		this.criteria = criteria;
		this.match = match;
	}
	
	private Coordinates getGlobalCenter(World world) {
		if(globalCenter != null)
			return globalCenter;
		Coordinates center = world.getSpawnOracle().get();
		return center == null ? Coordinates.origin() : center;
	}
		
	public boolean isValid(World world) {	
		//TODO
		return true;
	}
}
