package amidst.threading.worker;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

@FunctionalInterface
public interface ProgressReportingExceptionalWorker<P> {
	@CalledOnlyBy(AmidstThread.WORKER)
	void run(ProgressReporter<P> progressReporter) throws Exception;
}
