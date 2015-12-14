package amidst.threading;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

public interface Worker<T> {
	@CalledOnlyBy(AmidstThread.WORKER)
	T execute() throws Exception;

	@CalledOnlyBy(AmidstThread.EDT)
	void error(Exception e);

	@CalledOnlyBy(AmidstThread.EDT)
	void finished(T result);
}
