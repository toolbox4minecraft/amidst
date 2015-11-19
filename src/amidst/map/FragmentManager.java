package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import amidst.logging.Log;

public class FragmentManager {
	private ConcurrentLinkedQueue<Fragment> fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> requestQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<Fragment>();

	private FragmentCache cache;

	private ScheduledExecutorService executor;
	private int numberOfThreads;

	public FragmentManager(LayerContainer layerContainer) {
		this.cache = new FragmentCache(layerContainer, fragmentQueue);
		this.numberOfThreads = Runtime.getRuntime().availableProcessors() / 2;
		if (this.numberOfThreads < 2) {
			this.numberOfThreads = 2;
		}
	}

	public Fragment requestFragment(int x, int y) {
		Fragment fragment;
		while ((fragment = fragmentQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.initialize(x, y);
		requestQueue.offer(fragment);
		return fragment;
	}

	public void repaintFragmentImageLayers(Fragment fragment) {
		fragment.repaintAllImageLayers();
	}

	public void repaintFragmentImageLayer(Fragment fragment, int layerId) {
		fragment.repaintImageLayer(layerId);
	}

	public void recycleFragment(Fragment fragment) {
		recycleQueue.offer(fragment);
	}

	public void start() {
		initExecutor();
		startQueueProcessor(executor, numberOfThreads - 1, new Runnable() {
			@Override
			public void run() {
				processRequestQueue();
			}
		});
		startQueueProcessor(executor, 1, new Runnable() {
			@Override
			public void run() {
				processRecycleQueue();
			}
		});
	}

	private void initExecutor() {
		this.executor = Executors.newScheduledThreadPool(numberOfThreads,
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setDaemon(true);
						thread.setPriority(Thread.MIN_PRIORITY);
						return thread;
					}
				});
	}

	private void startQueueProcessor(ScheduledExecutorService executor,
			int amount, Runnable runnable) {
		for (int i = 0; i < amount; i++) {
			executor.scheduleWithFixedDelay(runnable, 0, 10,
					TimeUnit.MILLISECONDS);
		}
	}

	private void processRequestQueue() {
		while (!requestQueue.isEmpty()) {
			Fragment fragment = requestQueue.poll();
			if (fragment.needsLoading()) {
				fragment.load();
			}
		}
	}

	private void processRecycleQueue() {
		while (!recycleQueue.isEmpty()) {
			Fragment fragment = recycleQueue.poll();
			fragment.reset();
			fragmentQueue.offer(fragment);
		}
	}

	public void reset() {
		recycleQueue.clear();
		requestQueue.clear();
		fragmentQueue.clear();
		cache.resetAllFragments();
	}

	public void stop() {
		reset();
		shutdownExecutor();
	}

	private void shutdownExecutor() {
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log.w("FragmentManager executor took too long to shutdown ... forcing shutdown");
			executor.shutdownNow();
		}
	}

	public int getFreeFragmentQueueSize() {
		return fragmentQueue.size();
	}

	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}

	public int getRequestQueueSize() {
		return requestQueue.size();
	}

	public int getCacheSize() {
		return cache.size();
	}
}
