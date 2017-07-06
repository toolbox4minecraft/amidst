package amidst.filter;

import java.util.Optional;

import amidst.documentation.Immutable;
import amidst.filter.WorldFilterResult.ResultItem;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;

@Immutable
public interface Constraint {
	
	public Region getRegion();
	
	public Optional<Coordinates> checkRegion(World world, Region.Box region);
	
	public void addMarkers(ResultItem item);
	
	public boolean equals(Object other);
	public int hashCode();

}
