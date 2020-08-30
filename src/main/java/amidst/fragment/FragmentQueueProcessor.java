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
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> recycleQueue;
	private final AvailableFragmentCache availableCache;
	private final OffScreenFragmentCache offscreenCache;
	private final LayerManager layerManager;
	private final Setting<Dimension> dimensionSetting;
	private final FragmentGraph graph;

	@CalledByAny
	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> recycleQueue,
			AvailableFragmentCache availableCache,
			OffScreenFragmentCache offscreenCache,
			LayerManager layerManager,
			Setting<Dimension> dimensionSetting,
			FragmentGraph graph) {
		this.loadingQueue = loadingQueue;
		this.recycleQueue = recycleQueue;
		this.availableCache = availableCache;
		this.offscreenCache = offscreenCache;
		this.layerManager = layerManager;
		this.dimensionSetting = dimensionSetting;
		this.graph = graph;
	}

	/**
	 * Return the next fragment the loader should process, or null if no more fragments are available.
	 */
	private Fragment getNextFragment() {
		return loadingQueue.poll();
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
		while ((fragment = getNextFragment()) != null) {
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
			reloadAll();
			offscreenCache.clear();
		}
	}

	/**
	 * Gets all of the fragments currently on the graph to offer, as they aren't stored in any cache.
	 */
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private synchronized void reloadAll() {
		loadingQueue.clear();
		for (FragmentGraphItem graphItem : (Iterable<FragmentGraphItem>) () -> graph.iterator()) {
			loadingQueue.offer(graphItem.getFragment());
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
		availableCache.put(fragment);
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
