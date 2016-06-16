package amidst.mojangapi.world.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CriterionOr extends Criterion {

	private List<Criterion> criteria;
	private Result result = Result.UNKNOWN;
	
	public CriterionOr() {
	}
	
	public<T> CriterionOr(List<? extends T> criteria, Function<T, ? extends Criterion> mapper) {
		this.criteria = new ArrayList<>(criteria.size());
		for(T c: criteria) {
			this.criteria.add(mapper.apply(c));
		}
	}
	
	public CriterionOr(List<Criterion> criteria) {
		this.criteria = new ArrayList<>(criteria);
	}
	
	@Override
	public void forEachConstraint(Consumer<Constraint> consumer) {
		for(Criterion c: criteria)
			c.forEachConstraint(consumer);
	}

	@Override
	public Result isSatisfied(boolean canBeUnknown) {
		if(result != Result.UNKNOWN)
			return result;
		
		criteria.removeIf(c -> {
			if(result == Result.TRUE)
				return false;
			
			Result r = c.isSatisfied(canBeUnknown);
			if(r == Result.UNKNOWN)
				return false;
			
			if(r == Result.TRUE)
				result = r;
			return true;
		});
		

		if(result != Result.TRUE && criteria.isEmpty())
			result = Result.FALSE;
		
		if(result != Result.UNKNOWN)
			criteria = Collections.emptyList();
		
		return result;
	}

}
