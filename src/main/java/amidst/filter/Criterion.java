package amidst.filter;

import java.util.List;
import java.util.Objects;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.util.TriState;

@Immutable
public interface Criterion<R extends CriterionResult> {
	
	public R createResult();
	
	public Region.Box getNextRegionToCheck(ResultsMap map);
	
	public default TriState checkRegion(ResultsMap map, World world, Coordinates offset, Region.Box region) {
		Objects.requireNonNull(region);
		R res = map.get(this);
		res.checkRegionAndUpdate(map, world, offset, region);
		return res.hasMatched();
	}
	
	public List<Criterion<?>> getChildren();
}
