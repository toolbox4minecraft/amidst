package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledBy;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.layer.LayerLoader;
import amidst.mojangapi.world.CoordinatesInWorld;

/**
 * This class is thread-safe as long as only one thread calls the method tick().
 */
public class FragmentManager {
	private final ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<Fragment>();
	private final FragmentCache cache;

	private volatile FragmentQueueProcessor queueProcessor;

	public FragmentManager(Iterable<FragmentConstructor> constructors,
			int numberOfLayers) {
		this.cache = new FragmentCache(availableQueue, loadingQueue,
				constructors, numberOfLayers);
	}

	@CalledBy(AmidstThread.EDT)
	@CalledByAny
	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.setCorner(coordinates);
		fragment.setInitialized();
		loadingQueue.offer(fragment);
		return fragment;
	}

	@CalledBy(AmidstThread.EDT)
	@CalledByAny
	public void recycleFragment(Fragment fragment) {
		recycleQueue.offer(fragment);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void tick() {
		FragmentQueueProcessor queueProcessor = this.queueProcessor;
		if (queueProcessor != null) {
			queueProcessor.tick();
		}
	}

	public void reloadLayer(int layerId) {
		FragmentQueueProcessor queueProcessor = this.queueProcessor;
		if (queueProcessor != null) {
			queueProcessor.invalidateLayer(layerId);
		}
	}

	public void setLayerLoader(LayerLoader layerLoader) {
		this.queueProcessor = new FragmentQueueProcessor(availableQueue,
				loadingQueue, recycleQueue, cache, layerLoader);
	}

	public int getAvailableQueueSize() {
		return availableQueue.size();
	}

	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}

	public int getCacheSize() {
		return cache.size();
	}
}
