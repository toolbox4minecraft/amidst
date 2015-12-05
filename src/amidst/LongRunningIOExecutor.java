package amidst;

public class LongRunningIOExecutor {
	private final ThreadMaster threadMaster;

	public LongRunningIOExecutor(ThreadMaster threadMaster) {
		this.threadMaster = threadMaster;
	}

	public void invoke(Runnable runnable) {
		threadMaster.invokeLongRunningIOOperation(runnable);
	}
}
