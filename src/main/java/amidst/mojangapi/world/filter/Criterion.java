package amidst.mojangapi.world.filter;

import java.util.function.Consumer;

public abstract class Criterion {
	
	public static enum Result {
		TRUE, FALSE, UNKNOWN;
	}
	
	public abstract void forEachConstraint(Consumer<Constraint> consumer);
	
	public abstract Result isSatisfied(boolean canBeUnknown);
}
