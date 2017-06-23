package amidst.filter.criterion;

import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.filter.Criterion;

@Immutable
public class NegateCriterion implements Criterion {

	private final Criterion criterion;
	
	public NegateCriterion(String name, Criterion c) {
		criterion = c;
	}
	
	@Override
	public List<Criterion> getChildren() {
		return Collections.singletonList(criterion);
	}
}
