package amidst.threading;

import java.util.concurrent.ExecutorService;

import javax.swing.SwingUtilities;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class Worker<M, I, F> {
	private volatile ExecutorService executorService;

	@CalledByAny
	public void executeMain(ExecutorService executorService) {
		this.executorService = executorService;
		executorService.execute(new Runnable() {
			@CalledOnlyBy(AmidstThread.WORKER)
			@Override
			public void run() {
				try {
					executeOnMainFinishedLater(main());
				} catch (Exception e) {
					executeOnMainFinishedWithExceptionLater(e);
				}
			}
		});
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void executeOnMainFinishedLater(final M result) {
		SwingUtilities.invokeLater(new Runnable() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void run() {
				onMainFinished(result);
			}
		});
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void executeOnMainFinishedWithExceptionLater(final Exception e) {
		SwingUtilities.invokeLater(new Runnable() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void run() {
				onMainFinishedWithException(e);
			}
		});
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	protected void executeFork(final I input) {
		executorService.execute(new Runnable() {
			@CalledOnlyBy(AmidstThread.WORKER)
			@Override
			public void run() {
				try {
					executeOnForkFinishedLater(fork(input));
				} catch (Exception e) {
					executeOnForkFinishedWithExceptionLater(e);
				}
			}
		});
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void executeOnForkFinishedLater(final F result) {
		SwingUtilities.invokeLater(new Runnable() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void run() {
				onForkFinished(result);
			}
		});
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void executeOnForkFinishedWithExceptionLater(final Exception e) {
		SwingUtilities.invokeLater(new Runnable() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void run() {
				onForkFinishedWithException(e);
			}
		});
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
				"you need to override the method Worker.onMainFinishedWithException()",
				e);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	protected F fork(I input) throws Exception {
		throw new UnsupportedOperationException(
				"you need to override the method Worker.fork()");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void onForkFinished(F result) {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void onForkFinishedWithException(Exception e) {
		throw new UnsupportedOperationException(
				"you need to override the method Worker.onForkFinishedWithException()",
				e);
	}
}
