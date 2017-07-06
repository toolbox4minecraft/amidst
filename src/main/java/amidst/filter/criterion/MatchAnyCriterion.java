package amidst.filter.criterion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
public class MatchAnyCriterion implements Criterion<MatchAnyCriterion.Result> {
	
	private final List<Criterion<?>> criteria;
	
	public MatchAnyCriterion(List<Criterion<?>> list) {
		criteria = Collections.unmodifiableList(new ArrayList<>(list));
	}
	
	@Override
	public List<Criterion<?>> getChildren() {
		return criteria;
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
			
		for(Criterion<?> c: res.undecided) {
			Region.Box r = c.getNextRegionToCheck(map);
			if(r != null)
				return r;
		}
		
		return null;
	}
	
	public class Result implements CriterionResult {
		private List<Criterion<?>> undecided;
		private boolean isMatch;
		
		private Result() {
			this.undecided = new ArrayList<>(criteria);
			this.isMatch = false;
		}
		
		private Result(Result r) {
			this.undecided = new ArrayList<>(r.undecided);
			this.isMatch = r.isMatch;
		}
		
		@Override
		public TriState hasMatched() {
			if(isMatch)
				return TriState.TRUE;
			if(undecided.isEmpty())
				return TriState.FALSE;
			return TriState.UNKNOWN;
		}
		
		@Override
		public void checkRegionAndUpdate(ResultsMap map, World world, Coordinates offset, Region.Box region) {
			Iterator<Criterion<?>> iter = undecided.iterator();
			while(iter.hasNext()) {			
				TriState match = iter.next().checkRegion(map, world, offset, region);
				
				if(match != TriState.UNKNOWN)
					iter.remove();
				
				if(match == TriState.TRUE) {
					isMatch = true;
					undecided.clear();
					break;
				}
			}
		}

		@Override
		public CriterionResult copy() {
			return new Result(this);
		}
		
	}
}
