package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FragmentManager {
	private ConcurrentLinkedQueue<Fragment> fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> requestQueue = new ConcurrentLinkedQueue<Fragment>();

	private FragmentCache cache;

	public FragmentManager(LayerContainer layerContainer) {
		this.cache = new FragmentCache(layerContainer, fragmentQueue,
				requestQueue);
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

	public void tick() {
		processRequestQueue();
	}

	private void processRequestQueue() {
		while (!requestQueue.isEmpty()) {
			requestQueue.poll().load();
		}
	}

	public void reset() {
		requestQueue.clear();
		fragmentQueue.clear();
		cache.resetAll();
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

	public void reloadAll() {
		cache.reloadAll();
	}
}
