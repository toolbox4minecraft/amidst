package amidst.threading.worker;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

@FunctionalInterface
public interface ProgressReportingWorkerWithResult<R, P> {
	@CalledOnlyBy(AmidstThread.WORKER)
	R run(ProgressReporter<P> progressReporter);
}
