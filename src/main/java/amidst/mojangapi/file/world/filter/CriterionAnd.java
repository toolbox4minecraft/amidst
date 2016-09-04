package amidst.mojangapi.file.world.filter;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import amidst.mojangapi.world.coordinates.Region;

public class CriterionAnd implements Criterion {

	private String name;
	private List<Criterion> criteria;
	
	public CriterionAnd(String name, List<Criterion> list) {
		this.name = name;
		criteria = new ArrayList<>(list);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<Region> getBiomeRegionsNeeded() {
		return criteria.stream()
					.flatMap(c -> c.getBiomeRegionsNeeded().stream())
					.collect(Collectors.toSet());
	}
}
