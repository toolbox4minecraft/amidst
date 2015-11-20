package amidst;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadMaster {
	private Application application;

	private ScheduledExecutorService repainter;
	private ScheduledExecutorService fragmentLoader;
	private ExecutorService skinLoader;

	public ThreadMaster(Application application) {
		this.application = application;
		initRepainter();
		initFragmentLoader();
		initSkinLoader();
		dummyGUIThread();
		startRepainter();
		startFragmentLoader();
	}

	/**
	 * The main loop is responsible to constantly repaint the map in the
	 * mapViewer.
	 */
	private void initRepainter() {
		repainter = Executors
				.newSingleThreadScheduledExecutor(new ThreadFactory() {
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
	 * of biome data, the rendering of image layer and the creation of map
	 * objects. Note that this thread is not allowed to alter the fragment
	 * graph.
	 */
	private void initFragmentLoader() {
		fragmentLoader = Executors
				.newSingleThreadScheduledExecutor(new ThreadFactory() {
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
	 * The skin loader does not run constantly and is not a single thread. When
	 * skins are loaded, new threads will be spawned as needed. This is not an
	 * issue, since all of these threads are waiting for IO most of the time.
	 */
	private void initSkinLoader() {
		skinLoader = Executors.newCachedThreadPool(new ThreadFactory() {
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

	private void startRepainter() {
		repainter.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				application.tickRepainter();
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}

	private void startFragmentLoader() {
		fragmentLoader.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				application.tickFragmentLoader();
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}

	public void invokeSkinLoader(Runnable runnable) {
		skinLoader.execute(runnable);
	}
}
