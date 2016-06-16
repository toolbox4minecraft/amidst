package amidst.mojangapi.world.filter;

import java.util.HashSet;
import java.util.Set;

import amidst.mojangapi.file.json.filter.WorldFilterJson;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.filter.Criterion.Result;

public class WorldFilter {
	
	Set<Region.Box> regions = new HashSet<>();
	Set<ConstraintBiome> biomes = new HashSet<>();
	WorldFilterJson filter;
	
	public static final Resolution REGION_SIZE = Resolution.FRAGMENT;
	public static final Resolution GRANULARITY = Resolution.NETHER;
	public static final int BIOME_DATA_SIZE = REGION_SIZE.getStep()/GRANULARITY.getStep();
	public static final short[][] BIOME_DATA = new short[BIOME_DATA_SIZE][BIOME_DATA_SIZE];
	
	public WorldFilter(WorldFilterJson worldFilterJson) {
		filter = worldFilterJson;
	}

	public boolean hasFilters() {
		return filter != null;
	}
	
	public boolean isValid(World world) {
		return testCriterion(world, filter.getAsCriterion(world));
	}
	
	private boolean testCriterion(World world, Criterion criterion) {
		regions.clear();
		biomes.clear();
		
		criterion.forEachConstraint(c -> {
			if(!(c instanceof ConstraintBiome))
				return;
			
			biomes.add((ConstraintBiome) c);			
			Region r = ((ConstraintBiome) c).getRegion();
			CoordinatesInWorld corner = r.getCorner();
			long xMin = corner.snapXTo(REGION_SIZE);
			long yMin = corner.snapYTo(REGION_SIZE);
			long xMax = corner.getX() + r.getWidth();
			long yMax = corner.getY() + r.getHeight();
			int step = REGION_SIZE.getStep();
			
			for(long i = xMin; i < xMax; i += step) {
				for(long j = yMin; j < yMax; j += step) {
					Region.Box r2 = Region.box(i, j, step, step);
					if(r.intersectsWith(r2))
						regions.add(r2);
				}
			}
		});
		
		Result res = Result.UNKNOWN;
		for(Region.Box region: regions) {
			
			ConstraintBiome.checkRegionForBiomes(world, criterion, region, GRANULARITY, BIOME_DATA);
			res = criterion.isSatisfied(true);
			if(res != Result.UNKNOWN)
				break;
		}
		
		if(res == Result.UNKNOWN)
			res = criterion.isSatisfied(false);
		
		return res == Result.TRUE;
		
	}
}
