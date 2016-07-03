package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerManager;
import amidst.mojangapi.world.Dimension;
import amidst.settings.Setting;

@NotThreadSafe
public class FragmentQueueProcessor {
	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> recycleQueue;
	private final FragmentCache cache;
	private final LayerManager layerManager;
	private final Setting<Dimension> dimensionSetting;

	@CalledByAny
	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> recycleQueue,
			FragmentCache cache,
			LayerManager layerManager,
			Setting<Dimension> dimensionSetting) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.recycleQueue = recycleQueue;
		this.cache = cache;
		this.layerManager = layerManager;
		this.dimensionSetting = dimensionSetting;
	}

	/**
	 * It is important that the dimension setting is the same while a fragment
	 * is loaded by different fragment loaders. This is why the dimension
	 * setting is read by the fragment loader thread.
	 */
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void processQueues() {
		Dimension dimension = dimensionSetting.get();
		updateLayerManager(dimension);
		processRecycleQueue();
		Fragment fragment;
		while ((fragment = loadingQueue.poll()) != null) {
			loadFragment(dimension, fragment);
			dimension = dimensionSetting.get();
			updateLayerManager(dimension);
			processRecycleQueue();
		}
		layerManager.clearInvalidatedLayers();
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void updateLayerManager(Dimension dimension) {
		if (layerManager.updateAll(dimension)) {
			cache.reloadAll();
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void processRecycleQueue() {
		Fragment fragment;
		while ((fragment = recycleQueue.poll()) != null) {
			recycleFragment(fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void loadFragment(Dimension dimension, Fragment fragment) {
		if (fragment.isInitialized()) {
			if (fragment.isLoaded()) {
				layerManager.reloadInvalidated(dimension, fragment);
			} else {
				layerManager.loadAll(dimension, fragment);
				fragment.setLoaded();
			}
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void recycleFragment(Fragment fragment) {
		fragment.recycle();
		removeFromLoadingQueue(fragment);
		availableQueue.offer(fragment);
	}

	// TODO: Check performance with and without this. It is not needed, since
	// loadFragment checks for isInitialized(). It helps to keep the
	// loadingQueue small, but it costs time to remove fragments from the queue.
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void removeFromLoadingQueue(Object fragment) {
		while (loadingQueue.remove(fragment)) {
			// noop
		}
	}
}
