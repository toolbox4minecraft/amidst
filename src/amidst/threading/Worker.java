package amidst.threading;

public interface Worker<T> {
	T execute();

	void finished(T result);
}
