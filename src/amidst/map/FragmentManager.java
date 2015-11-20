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

	private FragmentCache cache;

	private ScheduledExecutorService executor;

	public FragmentManager(LayerContainer layerContainer) {
		this.cache = new FragmentCache(layerContainer, fragmentQueue);
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

	/**
	 * Make sure the passed fragment is no longer referenced by other fragments!
	 */
	public void recycleFragment(Fragment fragment) {
		fragment.reset();
		fragmentQueue.offer(fragment);
	}

	public void start() {
		initExecutor();
		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				processRequestQueue();
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
	}

	private void initExecutor() {
		this.executor = Executors
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

	private void processRequestQueue() {
		while (!requestQueue.isEmpty()) {
			Fragment fragment = requestQueue.poll();
			if (fragment.needsLoading()) {
				fragment.load();
			}
		}
	}

	public void reset() {
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

	public int getRequestQueueSize() {
		return requestQueue.size();
	}

	public int getCacheSize() {
		return cache.size();
	}
}
