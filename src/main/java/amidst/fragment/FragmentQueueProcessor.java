package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.LockSupport;

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
	private final ConcurrentLinkedDeque<Fragment> recycleQueue;
	private final AvailableFragmentCache availableCache;
	private final OffScreenFragmentCache offscreenCache;
	
	private final LayerManager layerManager;
	private final ThreadPoolExecutor fragWorkers;
	private final Setting<Dimension> dimensionSetting;
	private final FragmentGraph graph;

	@CalledByAny
	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedDeque<Fragment> recycleQueue,
			AvailableFragmentCache availableCache,
			OffScreenFragmentCache offscreenCache,
			LayerManager layerManager,
			Setting<Dimension> dimensionSetting,
			FragmentGraph graph,
			ThreadPoolExecutor fragWorkers) {
		this.loadingQueue = loadingQueue;
		this.recycleQueue = recycleQueue;
		this.availableCache = availableCache;
		this.offscreenCache = offscreenCache;
		
		this.layerManager = layerManager;
		this.dimensionSetting = dimensionSetting;
		this.graph = graph;
		this.fragWorkers = fragWorkers;
	}

	/**
	 * Returns if there are fragments that still need to be processed.
	 */
	private boolean hasFragments() {
		return !loadingQueue.isEmpty();
	}

	/**
	 * Return the next fragment the loader should process, or null if no more fragments are available.
	 */
	private Fragment getNextFragment() {
		return loadingQueue.poll();
	}

	private static final int PARK_MILLIS = 1000;

	/**
	 * It is important that the dimension setting is the same while a fragment
	 * is loaded by different fragment loaders. This is why the dimension
	 * setting is read by the fragment loader thread.
	 */
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void processQueues() {
		final Thread flThread = Thread.currentThread(); // the fragment loader thread
		Dimension dimension = dimensionSetting.get();
		updateLayerManager(dimension);
		processRecycleQueue();
		/*
		 * We queue fragments to the thread pool only when a thread isn't working.
		 * This keeps the thread pool queue small and doesn't make the fragment
		 * loading thread think we're done when we're still processing fragments.
		 * While the latter does happen a small amount with this setup, it's not
		 * to the extent of if we were pushing to the thread pool queue as fast
		 * as possible.
		 */
		int maxSize = fragWorkers.getMaximumPoolSize();
		while (hasFragments()) {
			if (fragWorkers.getActiveCount() < maxSize) {
				fragWorkers.execute(() -> {
					Fragment f = getNextFragment();
					if (f != null && dimension.equals(dimensionSetting.get())) {
						loadFragment(dimension, f);
						updateLayerManager(dimension);
						processRecycleQueue();
						LockSupport.unpark(flThread);
					}
				});
			} else {
				LockSupport.parkNanos(PARK_MILLIS * 1000000); // if for some reason unpark was never called, unpark after time expires
			}
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
			// the fragment goes back in the queue if it didn't fully recycle
			if (!recycleFragment(fragment)) {
				recycleQueue.addLast(fragment);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void loadFragment(Dimension dimension, Fragment fragment) {
		if (fragment.isInitialized()) {
			if (fragment.isLoaded()) {
				layerManager.reloadInvalidated(dimension, fragment);
			} else if (!fragment.getAndSetLoading()) {
				layerManager.loadAll(dimension, fragment);
				fragment.setLoaded();
			}
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private boolean recycleFragment(Fragment fragment) {		
		boolean recycled = fragment.recycle();
		if (recycled) {
			removeFromLoadingQueue(fragment);
			availableCache.put(fragment);
		}
		
		return recycled;
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
