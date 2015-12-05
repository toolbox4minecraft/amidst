package amidst;

public class LongRunningIOExecutor {
	private final ThreadMaster threadMaster;

	public LongRunningIOExecutor(ThreadMaster threadMaster) {
		this.threadMaster = threadMaster;
	}

	public void invokeLater(Runnable runnable) {
		threadMaster.invokeLongRunningIOOperation(runnable);
	}
}
