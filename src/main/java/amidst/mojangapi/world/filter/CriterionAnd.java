package amidst.mojangapi.world.filter;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.List;

import amidst.documentation.Immutable;

@Immutable
public class CriterionAnd implements Criterion {

	private final String name;
	private final List<Criterion> criteria;
	
	public CriterionAnd(String name, List<Criterion> list) {
		this.name = name;
		criteria = new ArrayList<>(list);
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
