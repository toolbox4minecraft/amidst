package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.LockSupport;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment.State;
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
	private final ThreadPoolExecutor fragWorkers;
	private final Setting<Dimension> dimensionSetting;

	@CalledByAny
	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> recycleQueue,
			FragmentCache cache,
			LayerManager layerManager,
			ThreadPoolExecutor fragWorkers,
			Setting<Dimension> dimensionSetting) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.recycleQueue = recycleQueue;
		this.cache = cache;
		this.layerManager = layerManager;
		this.dimensionSetting = dimensionSetting;
		this.fragWorkers = fragWorkers;
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
		while (loadingQueue.isEmpty() == false) {
			if (fragWorkers.getActiveCount() < maxSize) {
				fragWorkers.execute(() -> {
					Fragment f = loadingQueue.poll();
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
		if (fragment.getState().equals(Fragment.State.LOADED)) {
			layerManager.reloadInvalidated(dimension, fragment);
		} else if (!fragment.getState().equals(Fragment.State.UNINITIALIZED)
				&& !fragment.getAndSetState(Fragment.State.LOADING).equals(Fragment.State.LOADING)) {
			//If it's not loading, set loading and continue. If it is already loading, don't continue.
			layerManager.loadAll(dimension, fragment);
			fragment.setState(State.LOADED);
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void recycleFragment(Fragment fragment) {
		if (fragment.tryRecycle()) {
			removeFromLoadingQueue(fragment);
			availableQueue.offer(fragment);
		}
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
