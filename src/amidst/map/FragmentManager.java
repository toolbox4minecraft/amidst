package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FragmentManager {
	private ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();

	private int[] imageCache = new int[Fragment.SIZE * Fragment.SIZE];

	private FragmentCache cache;

	public FragmentManager(LayerContainer layerContainer) {
		this.cache = new FragmentCache(layerContainer, availableQueue,
				loadingQueue);
	}

	public Fragment requestFragment(int x, int y) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.initialize(x, y);
		loadingQueue.offer(fragment);
		return fragment;
	}

	/**
	 * Make sure the passed fragment is no longer referenced by other fragments!
	 */
	public void recycleFragment(Fragment fragment) {
		fragment.reset();
		availableQueue.offer(fragment);
	}

	public void tick() {
		processRequestQueue();
	}

	private void processRequestQueue() {
		while (!loadingQueue.isEmpty()) {
			loadingQueue.poll().load(imageCache);
		}
	}

	public void reset() {
		loadingQueue.clear();
		availableQueue.clear();
		cache.resetAll();
	}

	public int getAvailableQueueSize() {
		return availableQueue.size();
	}

	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	public int getCacheSize() {
		return cache.size();
	}

	public void reloadAll() {
		cache.reloadAll();
	}
}
