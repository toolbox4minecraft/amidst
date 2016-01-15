package amidst.threading.worker;

public interface ProgressReporter<P> {
	void report(P data);
}
