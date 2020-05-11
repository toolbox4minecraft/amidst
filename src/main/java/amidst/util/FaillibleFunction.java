package amidst.util;

@FunctionalInterface
public interface FaillibleFunction<T, U, E extends Throwable> {
	public U apply(T value) throws E;
}
