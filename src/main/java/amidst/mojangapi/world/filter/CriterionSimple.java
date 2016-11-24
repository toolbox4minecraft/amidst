package amidst.mojangapi.world.filter;

import java.util.stream.Stream;

import amidst.documentation.Immutable;

@Immutable
public class CriterionSimple implements Criterion {

	private final String name;
	private final Constraint constraint;
	
	public CriterionSimple(String n, Constraint c) {
		name = n;
		constraint = c;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Stream<Constraint> getConstraintStream() {
		return Stream.of(constraint);
	}

}
