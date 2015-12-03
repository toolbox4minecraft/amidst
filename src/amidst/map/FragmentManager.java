package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.layer.LayerManager;
import amidst.minecraft.world.CoordinatesInWorld;

public class FragmentManager {
	private final ConcurrentLinkedQueue<FragmentGraphItem> availableQueue = new ConcurrentLinkedQueue<FragmentGraphItem>();
	private final ConcurrentLinkedQueue<FragmentGraphItem> loadingQueue = new ConcurrentLinkedQueue<FragmentGraphItem>();
	private final ConcurrentLinkedQueue<FragmentGraphItem> resetQueue = new ConcurrentLinkedQueue<FragmentGraphItem>();
	private final FragmentCache cache;

	private volatile FragmentQueueProcessor queueProcessor;

	public FragmentManager(Iterable<FragmentConstructor> constructors,
			int numberOfLayers) {
		this.cache = new FragmentCache(availableQueue, loadingQueue,
				constructors, numberOfLayers);
	}

	public FragmentGraphItem requestFragment(CoordinatesInWorld coordinates) {
		FragmentGraphItem fragment;
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
	public void recycleFragment(FragmentGraphItem fragment) {
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
			loadingQueue.clear();
			cache.reloadAll();
		}
	}

	public void setLayerManager(LayerManager layerManager) {
		this.queueProcessor = new FragmentQueueProcessor(availableQueue,
				loadingQueue, resetQueue, layerManager);
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
