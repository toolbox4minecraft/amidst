package amidst.filter.criterion;

import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.filter.Criterion;

import java.util.Collections;

@Immutable
public class MatchAllCriterion implements Criterion {

	private final List<Criterion> criteria;
	
	public MatchAllCriterion(List<Criterion> list) {
		criteria = Collections.unmodifiableList(new ArrayList<>(list));
	}
	
	@Override
	public List<Criterion> getChildren() {
		return criteria;
	}
}
