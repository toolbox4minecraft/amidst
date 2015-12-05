package amidst;

public interface Worker<T> {
	T execute();

	void finished(T result);
}
