package amidst.threading;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import amidst.documentation.ThreadSafe;
import amidst.threading.worker.ExceptionalWorker;
import amidst.threading.worker.ExceptionalWorkerWithResult;
import amidst.threading.worker.ProgressReporter;
import amidst.threading.worker.ProgressReportingExceptionalWorker;
import amidst.threading.worker.ProgressReportingExceptionalWorkerWithResult;
import amidst.threading.worker.ProgressReportingWorker;
import amidst.threading.worker.ProgressReportingWorkerWithResult;
import amidst.threading.worker.Worker;
import amidst.threading.worker.WorkerWithResult;

@ThreadSafe
public class WorkerExecutor {
	private final ExecutorService executorService;

	public WorkerExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void run(Worker main) {
		runInWorker(() -> {
			main.run();
		});
	}

	public void run(ExceptionalWorker main, Consumer<Exception> onException) {
		runInWorker(() -> {
			try {
				main.run();
			} catch (Exception e) {
				runInEDT(() -> onException.accept(e));
			}
		});
	}

	public <P> void run(ProgressReportingWorker<P> main, Consumer<P> onProgress) {
		runInWorker(() -> {
			main.run(progressReporter(onProgress));
		});
	}

	public <P> void run(
			ProgressReportingExceptionalWorker<P> main,
			Consumer<P> onProgress,
			Consumer<Exception> onException) {
		runInWorker(() -> {
			try {
				main.run(progressReporter(onProgress));
			} catch (Exception e) {
				runInEDT(() -> onException.accept(e));
			}
		});
	}

	public void run(Worker main, Runnable onFinished) {
		runInWorker(() -> {
			main.run();
			runInEDT(() -> onFinished.run());
		});
	}

	public <R> void run(WorkerWithResult<R> main, Consumer<R> onFinished) {
		runInWorker(() -> {
			R output = main.run();
			runInEDT(() -> onFinished.accept(output));
		});
	}

	public void run(ExceptionalWorker main, Runnable onFinished, Consumer<Exception> onException) {
		runInWorker(() -> {
			try {
				main.run();
				runInEDT(() -> onFinished.run());
			} catch (Exception e) {
				runInEDT(() -> onException.accept(e));
			}
		});
	}

	public <R> void run(ExceptionalWorkerWithResult<R> main, Consumer<R> onFinished, Consumer<Exception> onException) {
		runInWorker(() -> {
			try {
				R output = main.run();
				runInEDT(() -> onFinished.accept(output));
			} catch (Exception e) {
				runInEDT(() -> onException.accept(e));
			}
		});
	}

	public <P> void run(ProgressReportingWorker<P> main, Consumer<P> onProgress, Runnable onFinished) {
		runInWorker(() -> {
			main.run(progressReporter(onProgress));
			runInEDT(() -> onFinished.run());
		});
	}

	public <R, P> void run(
			ProgressReportingWorkerWithResult<R, P> main,
			Consumer<P> onProgress,
			Consumer<R> onFinished) {
		runInWorker(() -> {
			R output = main.run(progressReporter(onProgress));
			runInEDT(() -> onFinished.accept(output));
		});
	}

	public <P> void run(
			ProgressReportingExceptionalWorker<P> main,
			Consumer<P> onProgress,
			Runnable onFinished,
			Consumer<Exception> onException) {
		runInWorker(() -> {
			try {
				main.run(progressReporter(onProgress));
				runInEDT(() -> onFinished.run());
			} catch (Exception e) {
				runInEDT(() -> onException.accept(e));
			}
		});
	}

	public <R, P> void run(
			ProgressReportingExceptionalWorkerWithResult<R, P> main,
			Consumer<P> onProgress,
			Consumer<R> onFinished,
			Consumer<Exception> onException) {
		runInWorker(() -> {
			try {
				R output = main.run(progressReporter(onProgress));
				runInEDT(() -> onFinished.accept(output));
			} catch (Exception e) {
				runInEDT(() -> onException.accept(e));
			}
		});
	}

	private <P> ProgressReporter<P> progressReporter(Consumer<P> onProgress) {
		return data -> reportProgress(onProgress, data);
	}

	private <P> void reportProgress(Consumer<P> onProgress, P data) {
		runInEDT(() -> onProgress.accept(data));
	}

	private void runInEDT(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}

	private void runInWorker(Runnable runnable) {
		executorService.execute(runnable);
	}
}
