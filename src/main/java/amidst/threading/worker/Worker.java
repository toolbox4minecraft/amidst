package amidst.threading.worker;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

@FunctionalInterface
public interface Worker {
	@CalledOnlyBy(AmidstThread.WORKER)
	void run();
}
