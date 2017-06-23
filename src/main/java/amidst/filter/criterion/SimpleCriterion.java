package amidst.filter.criterion;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import amidst.documentation.Immutable;
import amidst.filter.Constraint;
import amidst.filter.Criterion;
import amidst.filter.CriterionResult;
import amidst.filter.ResultsMap;
import amidst.filter.WorldFilter;
import amidst.filter.WorldFilterResult;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.util.TriState;

@Immutable
public class SimpleCriterion implements Criterion<SimpleCriterion.Result> {

	private final Constraint constraint;
	
	public SimpleCriterion(Constraint c) {
		constraint = c;
	}
	
	@Override
	public List<Criterion<?>> getChildren() {
		return Collections.emptyList();
	}
	
	@Override
	public Result createResult() {
		return new Result();
	}

	@Override
	public Region.Box getNextRegionToCheck(ResultsMap map) {
		Result res = map.get(this);
		if(res == null)
			return null;
		if(res.found != null || res.regionsToTest.isEmpty())
			return null;
		
		return res.regionsToTest.iterator().next();
	}
	
	public class Result implements CriterionResult {
		private Set<Region.Box> regionsToTest;
		Coordinates found = null;
		
		public Result() {
			regionsToTest = new HashSet<>();
			forEachSubRegion(constraint, regionsToTest::add);
		}
		
		public Result(Result other) {
			this.regionsToTest = new HashSet<>(other.regionsToTest);
		}

		@Override
		public TriState hasMatched() {
			if(found != null)
				return TriState.TRUE;
			if(regionsToTest.isEmpty())
				return TriState.FALSE;
			return TriState.UNKNOWN;
		}

		@Override
		public void checkRegionAndUpdate(ResultsMap map, World world, Coordinates offset, Region.Box region) {
			if(regionsToTest.remove(region)) {
				Optional<Coordinates> pos = constraint.checkRegion(world, region.move(offset));
				if(pos.isPresent())
					found = pos.get();
				regionsToTest.clear();
			}
		}
		
		@Override
		public void addItemToWorldResult(WorldFilterResult result) {
			if(found != null)
				constraint.addMarkers(result.getItemFor(found));
		}
		
		@Override
		public CriterionResult copy() {
			return new Result(this);
		}
		
	}
	
	private static void forEachSubRegion(Constraint constraint, Consumer<Region.Box> consumer) {
		Region region = constraint.getRegion();
		Coordinates c1 = region.getCornerNW().snapTo(WorldFilter.REGION_SIZE);
		Coordinates c2 = region.getCornerSE().snapUpwardsTo(WorldFilter.REGION_SIZE);
		
		int size = WorldFilter.REGION_SIZE.getStep();
		for(int x = c1.getX(); x < c2.getX(); x += size) {
			for(int y = c1.getY(); y < c2.getY(); y += size) {
				Region.Box r = Region.box(x, y, size, size);
				if(constraint.getRegion().intersectsWith(r))
					consumer.accept(r);
			}
		}
	}

}
