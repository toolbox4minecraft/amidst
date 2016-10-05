package amidst.mojangapi.world.filter;

import java.util.Set;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.Region;

@Immutable
public class CriterionNegate implements Criterion {
	
	private final String name;
	private final Criterion criterion;
	
	public CriterionNegate(String name, Criterion c) {
		this.name = name;
		criterion = c;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<Region> getBiomeRegionsNeeded() {
		return criterion.getBiomeRegionsNeeded();
	}
}
