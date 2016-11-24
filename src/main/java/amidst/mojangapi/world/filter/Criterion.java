package amidst.mojangapi.world.filter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import amidst.documentation.Immutable;

@Immutable
public interface Criterion {
	public String getName();
	
	public Stream<Constraint> getConstraintStream();
	
	public default Map<Constraint, Integer> getConstraints() {
		return getConstraintStream()
				.collect(Collectors.toMap(c -> c, c -> 1, (a, b) -> a + b));
	}
	
	//TODO
}
