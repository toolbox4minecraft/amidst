package amidst.threading;

import java.util.concurrent.ExecutorService;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class WorkerExecutor {
	private final ExecutorService executorService;

	public WorkerExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public <M, I, F> void invokeLater(Worker<M, I, F> worker) {
		worker.executeMain(executorService);
	}

	public <I> void invokeLater(WorkerWithoutResult<I> worker) {
		worker.executeMain(executorService);
	}

	public <M> void invokeLater(SimpleWorker<M> worker) {
		worker.executeMain(executorService);
	}

	public void invokeLater(SimpleWorkerWithoutResult worker) {
		worker.executeMain(executorService);
	}
}
