package amidst.threading;

public class WorkerExecutor {
	private final ThreadMaster threadMaster;

	public WorkerExecutor(ThreadMaster threadMaster) {
		this.threadMaster = threadMaster;
	}

	public void invokeLater(Runnable runnable) {
		threadMaster.executeWorker(runnable);
	}

	public <T> void invokeLater(Worker<T> worker) {
		threadMaster.executeWorker(worker);
	}
}
