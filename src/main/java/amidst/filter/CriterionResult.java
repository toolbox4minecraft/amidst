package amidst.filter;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.util.TriState;

public interface CriterionResult {
	public TriState hasMatched();
	
	public void checkRegionAndUpdate(ResultsMap map, World world, Coordinates offset, Region.Box region);
	
	public default void addItemToWorldResult(WorldFilterResult result) {
		//Do nothing
	}
	
	public CriterionResult copy();
	
}
