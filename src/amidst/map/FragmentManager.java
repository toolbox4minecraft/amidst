package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.logging.Log;

public class FragmentManager implements Runnable {
	private static final int INITIAL_CACHE_SIZE = 1024;

	private LayerContainer layerContainer;

	private Fragment[] fragmentCache = new Fragment[INITIAL_CACHE_SIZE];
	private ConcurrentLinkedQueue<Fragment> fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> requestQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<Fragment>();

	private Object queueLock = new Object();

	private Thread currentThread;
	private boolean running = true;
	private int sleepTick = 0;

	public FragmentManager(LayerContainer layerContainer) {
		this.layerContainer = layerContainer;
		fragmentCache = initWithFragments(fragmentCache, 0,
				fragmentCache.length);
	}

	private Fragment[] initWithFragments(Fragment[] fragmentCache, int from,
			int to) {
		for (int i = from; i < to; i++) {
			fragmentCache[i] = new Fragment(layerContainer);
			fragmentQueue.offer(fragmentCache[i]);
		}
		return fragmentCache;
	}

	private void increaseFragmentCache() {
		int currentCacheSize = fragmentCache.length;
		int newCacheSize = currentCacheSize << 1;
		Fragment[] newFragmentCache = newFromOldFragmentCache(newCacheSize);
		fragmentCache = initWithFragments(newFragmentCache, currentCacheSize,
				newCacheSize);
		Log.i("FragmentManager cache size increased from " + currentCacheSize
				+ " to " + newCacheSize);

		// TODO: do we really need to run the gc manually?
		System.gc();
	}

	private Fragment[] newFromOldFragmentCache(int newCacheSize) {
		Fragment[] newCache = new Fragment[newCacheSize];
		System.arraycopy(this.fragmentCache, 0, newCache, 0,
				this.fragmentCache.length);
		return newCache;
	}

	public Fragment requestFragment(int x, int y) {
		if (!running) {
			return null;
		}
		Fragment fragment;
		while ((fragment = fragmentQueue.poll()) == null) {
			increaseFragmentCache();
		}

		initFragment(fragment, x, y);
		requestQueue.offer(fragment);
		return fragment;
	}

	private void initFragment(Fragment fragment, int x, int y) {
		fragment.clear();
		fragment.blockX = x;
		fragment.blockY = y;
		fragment.isActive = true;
	}

	@Override
	public void run() {
		currentThread.setPriority(Thread.MIN_PRIORITY);

		while (running) {
			if (!requestQueue.isEmpty() || !recycleQueue.isEmpty()) {
				if (!requestQueue.isEmpty()) {
					processRequestQueueEntry();
				}
				while (!recycleQueue.isEmpty()) {
					processRecycleQueueEntry();
				}
			} else {
				sleepTick = 0;
				try {
					Thread.sleep(2);
				} catch (InterruptedException ignored) {
				}
			}
		}

	}

	private void processRecycleQueueEntry() {
		synchronized (queueLock) {
			Fragment fragment = recycleQueue.poll();
			fragment.recycle();
			fragmentQueue.offer(fragment);
		}
	}

	private void processRequestQueueEntry() {
		synchronized (queueLock) {
			Fragment fragment = requestQueue.poll();
			if (fragment.isActive && !fragment.isLoaded) {
				fragment.load();
				sleepIfNecessary();
			}
		}
	}

	private void sleepIfNecessary() {
		sleepTick++;
		if (sleepTick == 10) {
			sleepTick = 0;
			try {
				Thread.sleep(1);
			} catch (InterruptedException ignored) {
			}
		}
	}

	public void returnFragment(Fragment frag) {
		recycleQueue.offer(frag);
	}

	public void repaintFragment(Fragment frag) {
		synchronized (queueLock) {
			frag.repaint();
		}
	}

	public void repaintFragmentLayer(Fragment frag, int id) {
		synchronized (queueLock) {
			frag.repaintImageLayer(id);
		}
	}

	public void reset() {
		gracefullyShutdownCurrentThread();
		clearAllQueues();
		resetAllFragments();
	}

	public void setMap(Map map) {
		layerContainer.reloadAllLayers(map);
		startNewThread();
	}

	private void startNewThread() {
		running = true;
		currentThread = new Thread(this);
		currentThread.start();
	}

	private void gracefullyShutdownCurrentThread() {
		running = false;
		try {
			currentThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void resetAllFragments() {
		for (int i = 0; i < fragmentCache.length; i++) {
			fragmentCache[i].reset();
			fragmentQueue.offer(fragmentCache[i]);
		}
	}

	public void updateAllLayers(float time) {
		layerContainer.updateAllLayers(time);
	}

	private void clearAllQueues() {
		recycleQueue.clear();
		requestQueue.clear();
		fragmentQueue.clear();
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
		return fragmentCache.length;
	}
}
