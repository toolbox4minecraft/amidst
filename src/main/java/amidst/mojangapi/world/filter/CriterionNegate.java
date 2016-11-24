package amidst.mojangapi.world.filter;

import java.util.stream.Stream;

import amidst.documentation.Immutable;

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
	public Stream<Constraint> getConstraintStream() {
		return criterion.getConstraintStream();
	}
}
