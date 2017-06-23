package amidst.filter.criterion;

import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.filter.Constraint;
import amidst.filter.Criterion;

@Immutable
public class SimpleCriterion implements Criterion {

	private final Constraint constraint;
	
	public SimpleCriterion(Constraint c) {
		constraint = c;
	}
	
	@Override
	public List<Criterion> getChildren() {
		return Collections.emptyList();
	}
}
