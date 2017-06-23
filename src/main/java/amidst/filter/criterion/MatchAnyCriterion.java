package amidst.filter.criterion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.filter.Criterion;

@Immutable
public class MatchAnyCriterion implements Criterion {
	
	private final List<Criterion> criteria;
	
	public MatchAnyCriterion(List<Criterion> list) {
		criteria = Collections.unmodifiableList(new ArrayList<>(list));
	}
	
	@Override
	public List<Criterion> getChildren() {
		return criteria;
	}
}
