package amidst.mojangapi.world.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import amidst.documentation.Immutable;

@Immutable
public class CriterionOr implements Criterion {
	private final String name;
	private final List<Criterion> criteria;
	private final int minimum;
	
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
	public Stream<Constraint> getConstraintStream() {
		return criteria.stream().flatMap(Criterion::getConstraintStream);
	}
}
