package amidst.filter.criterion;

import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.filter.Criterion;
import amidst.filter.CriterionResult;
import amidst.filter.ResultsMap;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.util.TriState;

@Immutable
public class NegateCriterion implements Criterion<NegateCriterion.Result> {

	private final Criterion<?> criterion;
	
	public NegateCriterion(String name, Criterion<?> c) {
		criterion = c;
	}
	
	@Override
	public List<Criterion<?>> getChildren() {
		return Collections.singletonList(criterion);
	}

	@Override
	public Result createResult() {
		return new Result(TriState.UNKNOWN);
	}

	@Override
	public Region.Box getNextRegionToCheck(ResultsMap map) {
		return criterion.getNextRegionToCheck(map);
	}
	
	public class Result implements CriterionResult {
		TriState state;
		
		private Result(TriState state) {
			this.state = state;
		}

		@Override
		public TriState hasMatched() {
			return state;
		}

		@Override
		public void checkRegionAndUpdate(ResultsMap map, World world, Coordinates offset, Region.Box region) {
			if(state == TriState.UNKNOWN)
				state = criterion.checkRegion(map, world, offset, region).not();
		}

		@Override
		public CriterionResult copy() {
			return new Result(state);
		}
	}

}
