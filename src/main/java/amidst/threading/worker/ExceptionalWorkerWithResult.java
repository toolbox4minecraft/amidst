package amidst.threading.worker;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

@FunctionalInterface
public interface ExceptionalWorkerWithResult<R> {
	@CalledOnlyBy(AmidstThread.WORKER)
	R run() throws Exception;
}
