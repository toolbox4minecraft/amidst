package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.LockSupport;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerManager;
import amidst.mojangapi.world.Dimension;
import amidst.settings.Setting;

/**
 * The way multithreading is implemented here is pretty simple. First,
 * the ThreadPoolExecutor {@link #fragWorkers} is created when the
 * constructor of this class is made. Then, when
 * {@link #processQueues()} is called, we make sure that we are only
 * adding to the Executor queue when there are threads available to do
 * it. Otherwise, this thread parks until one of the threads in
 * fragWorkers notifies that it is ready to accept another request. If
 * this thread is never unparked by a thread in the thread pool, we
 * unpark after {@link #PARK_MILLIS} milliseconds so the program doesn't
 * hang.<br>
 * <br>
 * I'm very hesitant to remove the @NotThreadSafe annotation here due
 * to some of the methods here not being able to be called by multiple
 * threads. If someone in the future knows for sure that the methods
 * here are thread safe, please remove the annotation.
 */
@NotThreadSafe
public class FragmentQueueProcessor {
	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> recycleQueue;
	private final FragmentCache cache;
	private final LayerManager layerManager;
	private final Setting<Dimension> dimensionSetting;
	
	/**
	 * The thread pool used in fragment loading.
	 */
	private ThreadPoolExecutor fragWorkers;

	@CalledByAny
	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> recycleQueue,
			FragmentCache cache,
			LayerManager layerManager,
			Setting<Dimension> dimensionSetting,
			Setting<Integer> threadsSetting) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.recycleQueue = recycleQueue;
		this.cache = cache;
		this.layerManager = layerManager;
		this.dimensionSetting = dimensionSetting;
		
		this.fragWorkers = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadsSetting.get(), new ThreadFactory() {
			private int num;
		
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("Fragment-Worker-" + num++);
				return thread;
			}
		}
	);
	}
	
	private static final int PARK_MILLIS = 1000;
	
	/**
	 * It is important that the dimension setting is the same while a fragment
	 * is loaded by different fragment loaders. This is why the dimension
	 * setting is read by the fragment loader thread.<br><br>
	 * For information about how multithreading works here, see
	 * {@link FragmentQueueProcessor}
	 */
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void processQueues() {
		final Thread flThread = Thread.currentThread();
		Dimension dimension = dimensionSetting.get();
		updateLayerManager(dimension);
		processRecycleQueue();
		int maxSize = fragWorkers.getMaximumPoolSize();
		while (loadingQueue.isEmpty() == false) {
			if (fragWorkers.getActiveCount() < maxSize) {
				fragWorkers.execute(() -> {
					Fragment f = loadingQueue.poll();
					if (f != null && dimension.equals(dimensionSetting.get())) {
						loadFragment(dimension, f);
						updateLayerManager(dimension);
						processRecycleQueue();
						LockSupport.unpark(flThread);
					}
				});
			} else {
				LockSupport.parkNanos(PARK_MILLIS * 1000000); // if for some reason unpark was never called, unpark after 1 second
			}
		}
		layerManager.clearInvalidatedLayers();
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void updateLayerManager(Dimension dimension) {
		if (layerManager.updateAll(dimension)) {
			cache.reloadAll();
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void processRecycleQueue() {
		Fragment fragment;
		while ((fragment = recycleQueue.poll()) != null) {
			recycleFragment(fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void loadFragment(Dimension dimension, Fragment fragment) {
		if (fragment.isInitialized()) {
			if (fragment.isLoaded()) {
				layerManager.reloadInvalidated(dimension, fragment);
			} else {
				layerManager.loadAll(dimension, fragment);
				fragment.setLoaded();
			}
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void recycleFragment(Fragment fragment) {
		fragment.recycle();
		availableQueue.offer(fragment);
	}
	
}
