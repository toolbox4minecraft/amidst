package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.filter.Criterion;
import amidst.mojangapi.world.filter.CriterionAnd;
import amidst.mojangapi.world.filter.WorldFilter;
import amidst.mojangapi.world.filter.WorldFinder;

@Immutable
public class WorldFilterJson {
	private volatile boolean continuousSearch;
	private volatile List<CriterionJson> filters = Collections.emptyList();
	
	@GsonConstructor
	public WorldFilterJson() {
	}
	
	public boolean isValidState() {
		return true;
	}

	public void configureWorldFinder(WorldFinder worldFinder) {
		worldFinder.setWorldFilter(new WorldFilter(this));
		worldFinder.setContinuous(continuousSearch);
	}
	
	public Criterion getAsCriterion(World world) {
		if(filters.isEmpty())
			throw new IllegalStateException("must have at least one filter");
		return new CriterionAnd(filters, c -> c.getCriterion(world));
	}
}