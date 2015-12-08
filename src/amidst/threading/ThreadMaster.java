package amidst.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

public class ThreadMaster {
	private final Runnable onRepaintTick;
	private final Runnable onFragmentLoadTick;

	private final ScheduledExecutorService repaintExecutorService;
	private final ScheduledExecutorService fragmentLoaderExecutorService;
	private final ExecutorService workerExecutorService;
	private final WorkerExecutor workerExecutor;
	private final SkinLoader skinLoader;

	public ThreadMaster(Runnable onRepaintTick, Runnable onFragmentLoadTick) {
		this.onRepaintTick = onRepaintTick;
		this.onFragmentLoadTick = onFragmentLoadTick;
		this.repaintExecutorService = createRepaintExecutorService();
		this.fragmentLoaderExecutorService = createFragmentLoaderExecutorService();
		this.workerExecutorService = createWorkerExecutorService();
		dummyGUIThread();
		this.workerExecutor = createWorkerExecutor();
		this.skinLoader = createSkinLoader();
		startRepainter();
		startFragmentLoader();
	}

	/**
	 * The repainter is responsible to constantly repaint the gui.
	 */
	private ScheduledExecutorService createRepaintExecutorService() {
		return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	/**
	 * The fragment loader constantly loads fragments. This includes the loading
	 * of biome data, the rendering of image layers and the creation of world
	 * icons. Note that this thread is not allowed to alter the fragment graph.
	 */
	private ScheduledExecutorService createFragmentLoaderExecutorService() {
		return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				thread.setPriority(Thread.MIN_PRIORITY);
				return thread;
			}
		});
	}

	/**
	 * The worker executor does not run constantly and is not a single thread.
	 * New threads will be spawned as needed. This is not an issue, since it is
	 * only used for operations that wait for IO most of the time. For example,
	 * it is used for the skin loading.
	 */
	private ExecutorService createWorkerExecutorService() {
		return Executors.newCachedThreadPool(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				thread.setPriority(Thread.MIN_PRIORITY);
				return thread;
			}
		});
	}

	/**
	 * This is only a dummy method to remind the programmer that there is still
	 * another thread. When the application receives input from the GUI, the
	 * action is executed in the event thread of the GUI framework (AWT/Swing).
	 * Also, the actual repainting is triggered in this thread. Thus, it is the
	 * only thread that is allowed to alter and access the fragment graph. Note
	 * that other threads can modify individual fragments.
	 */
	private void dummyGUIThread() {
	}

	private WorkerExecutor createWorkerExecutor() {
		return new WorkerExecutor(this);
	}

	private SkinLoader createSkinLoader() {
		return new SkinLoader(workerExecutor);
	}

	private void startRepainter() {
		repaintExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				onRepaintTick.run();
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}

	private void startFragmentLoader() {
		fragmentLoaderExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				onFragmentLoadTick.run();
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}

	public <T> void executeWorker(final Worker<T> worker) {
		workerExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				callFinishedLater(worker, worker.execute());
			}
		});
	}

	private <T> void callFinishedLater(final Worker<T> worker, final T result) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				worker.finished(result);
			}
		});
	}

	public WorkerExecutor getWorkerExecutor() {
		return workerExecutor;
	}

	public SkinLoader getSkinLoader() {
		return skinLoader;
	}
}
