package amidst.threading;

import java.util.concurrent.ExecutorService;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class WorkerWithoutResult<I> {
	private final Worker<Void, I, Void> worker;

	public WorkerWithoutResult() {
		this.worker = new Worker<Void, I, Void>() {
			@Override
			protected Void main() throws Exception {
				WorkerWithoutResult.this.main();
				return null;
			}

			@Override
			protected void onMainFinished(Void result) {
				WorkerWithoutResult.this.onMainFinished();
			}

			@Override
			protected void onMainFinishedWithException(Exception e) {
				WorkerWithoutResult.this.onMainFinishedWithException(e);
			}

			@Override
			protected Void fork(I input) throws Exception {
				WorkerWithoutResult.this.fork(input);
				return null;
			}

			@Override
			protected void onForkFinished(Void result) {
				WorkerWithoutResult.this.onForkFinished();
			}

			@Override
			protected void onForkFinishedWithException(Exception e) {
				WorkerWithoutResult.this.onForkFinishedWithException(e);
			}
		};
	}

	@CalledByAny
	public void executeMain(ExecutorService executorService) {
		worker.executeMain(executorService);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	protected void executeFork(final I input) {
		worker.executeFork(input);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	protected abstract void main() throws Exception;

	@CalledOnlyBy(AmidstThread.EDT)
	protected void onMainFinished() {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void onMainFinishedWithException(Exception e) {
		throw new UnsupportedOperationException(
				"you need to override the method WorkerWithoutResult.onMainFinishedWithException()",
				e);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	protected void fork(I input) throws Exception {
		throw new UnsupportedOperationException(
				"you need to override the method WorkerWithoutResult.fork()");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void onForkFinished() {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void onForkFinishedWithException(Exception e) {
		throw new UnsupportedOperationException(
				"you need to override the method WorkerWithoutResult.onForkFinishedWithException()",
				e);
	}
}
