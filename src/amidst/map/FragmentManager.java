package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.minecraft.world.CoordinatesInWorld;

public class FragmentManager {
	private ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();

	private FragmentCache cache;
	private FragmentLoader loader;

	public FragmentManager(LayerContainer layerContainer) {
		this.cache = new FragmentCache(layerContainer, availableQueue,
				loadingQueue);
		this.loader = new FragmentLoader(layerContainer, loadingQueue);
	}

	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.initialize(coordinates);
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
		loader.processRequestQueue();
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
