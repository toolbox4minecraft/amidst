package amidst.mojangapi.world.filter;


import java.util.function.Consumer;

public class CriterionSimple extends Criterion {

	Constraint constraint;
	boolean isSatisfied;
	
	public CriterionSimple(Constraint c) {
		constraint = c;
	}
		
	@Override
	public void forEachConstraint(Consumer<Constraint> consumer) {
		if(!isSatisfied)
			consumer.accept(constraint);
	}

	@Override
	public Result isSatisfied(boolean canBeUnknown) {
		if(isSatisfied)
			return Result.TRUE;
		
		if(constraint.isSatisfied()) {
			isSatisfied = true;
			return Result.TRUE;
		}
		
		return canBeUnknown ? Result.UNKNOWN : Result.FALSE;
	}

	
}
