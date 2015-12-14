package amidst.threading;

import java.util.concurrent.ExecutorService;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;

public abstract class SimpleWorkerWithoutResult {
	@CalledByAny
	public void executeMain(ExecutorService executorService) {
		new Worker<Void, Void, Void>() {
			@Override
			protected Void main() throws Exception {
				SimpleWorkerWithoutResult.this.main();
				return null;
			}

			@Override
			protected void onMainFinished(Void result) {
				SimpleWorkerWithoutResult.this.onMainFinished();
			}

			@Override
			protected void onMainFinishedWithException(Exception e) {
				SimpleWorkerWithoutResult.this.onMainFinishedWithException(e);
			}
		}.executeMain(executorService);
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
				"you need to override the method SimpleWorkerWithoutResult.onMainFinishedWithException()",
				e);
	}
}
