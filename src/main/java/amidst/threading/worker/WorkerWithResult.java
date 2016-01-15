package amidst.threading.worker;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

@FunctionalInterface
public interface WorkerWithResult<R> {
	@CalledOnlyBy(AmidstThread.WORKER)
	R run();
}
