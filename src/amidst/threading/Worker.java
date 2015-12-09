package amidst.threading;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

public interface Worker<T> {
	@CalledOnlyBy(AmidstThread.WORKER)
	T execute();

	@CalledOnlyBy(AmidstThread.EDT)
	void finished(T result);
}
