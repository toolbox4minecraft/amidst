package amidst.threading;

import java.util.concurrent.ExecutorService;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;

public abstract class SimpleWorker<M> {
	@CalledByAny
	public void executeMain(ExecutorService executorService) {
		new Worker<M, Void, Void>() {
			@Override
			protected M main() throws Exception {
				return SimpleWorker.this.main();
			}

			@Override
			protected void onMainFinished(M result) {
				SimpleWorker.this.onMainFinished(result);
			}

			@Override
			protected void onMainFinishedWithException(Exception e) {
				SimpleWorker.this.onMainFinishedWithException(e);
			}
		}.executeMain(executorService);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	protected abstract M main() throws Exception;

	@CalledOnlyBy(AmidstThread.EDT)
	protected void onMainFinished(M result) {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void onMainFinishedWithException(Exception e) {
		throw new UnsupportedOperationException(
				"you need to override the method SimpleWorker.onMainFinishedWithException()",
				e);
	}
}
