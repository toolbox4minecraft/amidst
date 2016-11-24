package amidst.mojangapi.world.filter;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.Region;

@Immutable
public interface Constraint {
	
	public Region getBiomeRegion();
	
	public abstract boolean equals(Object other);
	public abstract int hashCode();
}
