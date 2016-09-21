package amidst.mojangapi.file.world.filter;

import java.util.Set;

import amidst.mojangapi.world.coordinates.Region;

public class CriterionNegate implements Criterion {
	
	private String name;
	private Criterion criterion;
	
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
