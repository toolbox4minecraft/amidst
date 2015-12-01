package amidst.map;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.minecraft.world.CoordinatesInWorld;

public class FragmentManager {
	private final ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> resetQueue = new ConcurrentLinkedQueue<Fragment>();
	private final FragmentCache cache;

	private volatile FragmentQueueProcessor queueProcessor;

	public FragmentManager(List<FragmentConstructor> constructors) {
		this.cache = new FragmentCache(availableQueue, loadingQueue,
				resetQueue, constructors);
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

	public void tick() {
		// Do not inline this variable to ensure there is only one read
		// operation.
		FragmentQueueProcessor queueProcessor = this.queueProcessor;
		if (queueProcessor != null) {
			queueProcessor.tick();
		}
	}

	public void setLayerManager(LayerManager layerManager) {
		this.queueProcessor = new FragmentQueueProcessor(availableQueue,
				loadingQueue, resetQueue, layerManager);
	}
}
