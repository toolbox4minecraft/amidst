package amidst.mojangapi.world.filter;

import java.util.List;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

public class WorldFilter {

	//if null, use world spawn point
	private CoordinatesInWorld globalCenter = null;
	
	private List<Criterion> criteria;
	
	private Criterion match;
	
	public WorldFilter(CoordinatesInWorld center, List<Criterion> criteria, Criterion match) {
		globalCenter = center;
		this.criteria = criteria;
		this.match = match;
	}
	
	private CoordinatesInWorld getGlobalCenter(World world) {
		if(globalCenter != null)
			return globalCenter;
		CoordinatesInWorld center = world.getSpawnOracle().get();
		return center == null ? CoordinatesInWorld.origin() : center;
	}
	
	public boolean isValid(World world) {
		throw new RuntimeException("not implemented!");
		//TODO
	}
}
