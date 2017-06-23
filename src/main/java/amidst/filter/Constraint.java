package amidst.filter;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.Region;

@Immutable
public interface Constraint {
	
	public Region getRegion();
	
	public boolean equals(Object other);
	public int hashCode();

}
