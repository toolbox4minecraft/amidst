package amidst.mojangapi.world.filter;

import java.util.function.Consumer;

public class CriterionNegate extends Criterion {
	
	Criterion criterion;
	
	public CriterionNegate(Criterion criterion) {
		this.criterion = criterion;
	}

	@Override
	public void forEachConstraint(Consumer<Constraint> consumer) {
		criterion.forEachConstraint(consumer);
	}

	@Override
	public Result isSatisfied(boolean canBeUnknown) {
		Result r = criterion.isSatisfied(canBeUnknown);
		if(r == Result.TRUE)
			return Result.FALSE;
		
		if(r == Result.FALSE)
			return Result.TRUE;
		
		return Result.UNKNOWN;
	}


}
