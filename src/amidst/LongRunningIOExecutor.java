package amidst;

import javax.swing.SwingUtilities;

public class LongRunningIOExecutor {
	private final ThreadMaster threadMaster;

	public LongRunningIOExecutor(ThreadMaster threadMaster) {
		this.threadMaster = threadMaster;
	}

	public void invokeLater(Runnable runnable) {
		threadMaster.invokeLongRunningIOOperation(runnable);
	}

	public <T> void invokeLater(final LongRunningIOOperation<T> operation) {
		threadMaster.invokeLongRunningIOOperation(new Runnable() {
			@Override
			public void run() {
				callFinishedLater(operation, operation.execute());
			}
		});
	}

	private <T> void callFinishedLater(
			final LongRunningIOOperation<T> operation, final T result) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				operation.finished(result);
			}
		});
	}
}
