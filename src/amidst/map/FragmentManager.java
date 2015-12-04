package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.layer.LayerLoader;
import amidst.minecraft.world.CoordinatesInWorld;

/**
 * This class is thread-safe as long as only one thread calls the method tick().
 */
public class FragmentManager {
	private final ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> resetQueue = new ConcurrentLinkedQueue<Fragment>();
	private final FragmentCache cache;

	private volatile FragmentQueueProcessor queueProcessor;

	public FragmentManager(Iterable<FragmentConstructor> constructors,
			int numberOfLayers) {
		this.cache = new FragmentCache(availableQueue, loadingQueue,
				constructors, numberOfLayers);
	}

	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.setCorner(coordinates);
		fragment.setInitialized(true);
		loadingQueue.offer(fragment);
		return fragment;
	}

	public void recycleFragment(Fragment fragment) {
		resetQueue.offer(fragment);
	}

	public void tick() {
		// Do not inline this variable to ensure there is only one read
		// operation.
		FragmentQueueProcessor queueProcessor = this.queueProcessor;
		if (queueProcessor != null) {
			queueProcessor.tick();
		}
	}

	public void reloadLayer(int layerId) {
		// Do not inline this variable to ensure there is only one read
		// operation.
		FragmentQueueProcessor queueProcessor = this.queueProcessor;
		if (queueProcessor != null) {
			queueProcessor.invalidateLayer(layerId);
		}
	}

	public void setLayerLoader(LayerLoader layerLoader) {
		this.queueProcessor = new FragmentQueueProcessor(availableQueue,
				loadingQueue, resetQueue, cache, layerLoader);
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
}
