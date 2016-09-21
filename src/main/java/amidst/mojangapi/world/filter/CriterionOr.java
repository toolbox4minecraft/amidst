package amidst.mojangapi.file.world.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import amidst.mojangapi.world.coordinates.Region;

public class CriterionOr implements Criterion {
	private String name;
	private List<Criterion> criteria;
	private int minimum;
	
	public CriterionOr(String name, List<Criterion> list, int min) {
		this.name = name;
		criteria = new ArrayList<>(list);
		minimum = min;
	}
	
	public CriterionOr(String name, List<Criterion> list) {
		this(name, list, 1);
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
