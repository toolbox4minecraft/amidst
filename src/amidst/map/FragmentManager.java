package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.minecraft.world.CoordinatesInWorld;

public class FragmentManager {
	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> resetQueue;
	private final FragmentCache cache;
	private final FragmentLoader loader;
	private LayerContainer layerContainer;

	public FragmentManager(ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> resetQueue, FragmentCache cache,
			FragmentLoader loader, LayerContainer layerContainer) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.resetQueue = resetQueue;
		this.cache = cache;
		this.loader = loader;
		this.layerContainer = layerContainer;
	}

	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.initialize(coordinates);
		fragment.setInitialized(true);
		loadingQueue.offer(fragment);
		return fragment;
	}

	/**
	 * Make sure the passed fragment is no longer referenced by other fragments!
	 */
	public void recycleFragment(Fragment fragment) {
		resetQueue.offer(fragment);
	}

	public void tick() {
		loader.tick();
	}

	public void reloadAll() {
		loadingQueue.clear();
		cache.reloadAll();
	}

	public void reset() {
		availableQueue.clear();
		loadingQueue.clear();
		resetQueue.clear();
		cache.resetAll();
	}

	public int getAvailableQueueSize() {
		return availableQueue.size();
	}

	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	public int getResetQueueSize() {
		return resetQueue.size();
	}

	public int getCacheSize() {
		return cache.size();
	}

	public LayerContainer getLayerContainer() {
		return layerContainer;
	}
}
