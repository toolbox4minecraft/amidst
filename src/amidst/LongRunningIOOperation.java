package amidst;

public interface LongRunningIOOperation<T> {
	T execute();

	void finished(T result);
}
