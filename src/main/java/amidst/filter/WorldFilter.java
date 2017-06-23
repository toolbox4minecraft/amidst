package amidst.filter;

import java.util.Map;
import java.util.Optional;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.oracle.CachedBiomeDataOracle;
import amidst.util.TriState;

public class WorldFilter {
	
	public static final Resolution REGION_SIZE = Resolution.FRAGMENT;

	//if null, use world spawn point
	private final Coordinates globalCenter;
	
	private final Map<String, Criterion<?>> criteria;
	private final Criterion<?> match;
	
	private final ResultsMap results = new ResultsMap();
	
	public WorldFilter(Coordinates center, Map<String, Criterion<?>> criteria, Criterion<?> match) {
		globalCenter = center;
		this.criteria = criteria;
		this.match = match;
		
		fillResultsMap(match);
		for(Criterion<?> c: criteria.values())
			fillResultsMap(c);
	}
	
	private void fillResultsMap(Criterion<?> criterion) {
		if(!results.create(criterion))
			return;
		
		for(Criterion<?> c: criterion.getChildren())
			fillResultsMap(c);
	}
	
	private Coordinates getGlobalCenter(World world) {
		if(globalCenter != null)
			return globalCenter;
		Coordinates center = world.getSpawnOracle().get();
		return center == null ? Coordinates.origin() : center;
	}
	
	private void addItemsOfMatchedCriteria(WorldFilterResult result, ResultsMap map, Criterion<?> criterion) {
		CriterionResult r = map.remove(criterion);
		if(r == null || r.hasMatched() != TriState.TRUE)
			return;
		
		r.addItemToWorldResult(result);
		
		for(Criterion<?> c: criterion.getChildren())
			addItemsOfMatchedCriteria(result, map, c);
		
	}

	private boolean isValid(ResultsMap map, World world, Coordinates offset, Criterion<?> criterion) {
		CriterionResult res = map.get(criterion);
		
		Region.Box region = criterion.getNextRegionToCheck(map);
		
		if(region == null)
			return true;
		
		world = world.cached(region);
		CachedBiomeDataOracle oracle = (CachedBiomeDataOracle) world.getBiomeDataOracle();
		
		while(criterion.checkRegion(map, world, offset, region) == TriState.UNKNOWN) {
			region = criterion.getNextRegionToCheck(map);
			oracle.moveCacheTo(region);
		}
		
		return res.hasMatched() == TriState.TRUE;
	}
	
	public Optional<WorldFilterResult> match(World world) {
		ResultsMap results = this.results.copy();
		
		Coordinates offset = getGlobalCenter(world);
		
		if(!isValid(results, world, offset, match))
			return Optional.empty();
		
		WorldFilterResult result = new WorldFilterResult(world);
		
		for(Map.Entry<String, Criterion<?>> e: criteria.entrySet()) {
			if(isValid(results, world, offset, e.getValue())) {
				result.addOptionalGoal(e.getKey());
				addItemsOfMatchedCriteria(result, results, e.getValue());
			}
		}
			
		return Optional.of(result);
	}
}
