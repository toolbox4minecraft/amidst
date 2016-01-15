package amidst.threading.worker;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

@FunctionalInterface
public interface ExceptionalWorker {
	@CalledOnlyBy(AmidstThread.WORKER)
	void run() throws Exception;
}
