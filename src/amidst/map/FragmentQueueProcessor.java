package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.layer.LayerManager;

public class FragmentQueueProcessor {
	private final TaskQueue taskQueue = new TaskQueue();

	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> resetQueue;
	private final FragmentCache cache;
	private final LayerManager layerManager;

	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> resetQueue, FragmentCache cache,
			LayerManager layerManager) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.resetQueue = resetQueue;
		this.cache = cache;
		this.layerManager = layerManager;
	}

	/**
	 * This method might be called from any thread.
	 */
	public void invalidateLayer(final int layerId) {
		taskQueue.invoke(new Runnable() {
			@Override
			public void run() {
				doInvalidateLayer(layerId);
			}
		});
	}

	private void doInvalidateLayer(int layerId) {
		layerManager.invalidateLayer(layerId);
		cache.reloadAll();
	}

	/**
	 * This method is only called from the fragment loading thread.
	 */
	public void tick() {
		taskQueue.processTasks();
		processResetQueue();
		Fragment fragment;
		while ((fragment = loadingQueue.poll()) != null) {
			loadFragment(fragment);
			taskQueue.processTasks();
			processResetQueue();
		}
		layerManager.clearInvalidatedLayers();
	}

	private void processResetQueue() {
		Fragment fragment;
		while ((fragment = resetQueue.poll()) != null) {
			resetFragment(fragment);
		}
	}

	private void loadFragment(Fragment fragment) {
		if (fragment.isInitialized()) {
			if (fragment.isLoaded()) {
				layerManager.reloadInvalidated(fragment);
			} else {
				layerManager.loadAll(fragment);
				fragment.setLoaded(true);
			}
		}
	}

	private void resetFragment(Fragment fragment) {
		fragment.setLoaded(false);
		fragment.setInitialized(false);
		removeFromLoadingQueue(fragment);
		availableQueue.offer(fragment);
	}

	// TODO: Check performance with and without this. It is not needed, since
	// loadFragment checks for isInitialized(). It helps to keep the
	// loadingQueue small, but it costs time to remove fragments from the queue.
	private void removeFromLoadingQueue(Object fragment) {
		while (loadingQueue.remove(fragment)) {
			// noop
		}
	}
}
