package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.layer.LayerManager;

public class FragmentQueueProcessor {
	private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();

	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> resetQueue;
	private final FragmentCache cache;
	private final LayerManager layerManager;

	private Fragment currentFragment;

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
		tasks.offer(new Runnable() {
			@Override
			public void run() {
				doInvalidateLayer(layerId);
			}
		});
	}

	private void doInvalidateLayer(final int layerId) {
		layerManager.invalidateLayer(layerId);
		cache.reloadAll();
	}

	/**
	 * This method is only called from the fragment loading thread.
	 */
	public void tick() {
		processTasks();
		processResetQueue();
		while ((currentFragment = loadingQueue.poll()) != null) {
			loadFragment();
			processTasks();
			processResetQueue();
		}
		layerManager.clearInvalidatedLayers();
	}

	private void processTasks() {
		Runnable task;
		while ((task = tasks.poll()) != null) {
			task.run();
		}
	}

	private void processResetQueue() {
		while ((currentFragment = resetQueue.poll()) != null) {
			resetFragment();
		}
	}

	private void loadFragment() {
		if (currentFragment.isInitialized()) {
			if (currentFragment.isLoaded()) {
				layerManager.reloadInvalidated(currentFragment);
			} else {
				layerManager.loadAll(currentFragment);
				currentFragment.setLoaded(true);
			}
		}
	}

	private void resetFragment() {
		currentFragment.setLoaded(false);
		currentFragment.setInitialized(false);
		removeFromLoadingQueue();
		availableQueue.offer(currentFragment);
	}

	// TODO: Check performance with and without this. It is not needed, since
	// loadFragment checks for isInitialized(). It helps to keep the
	// loadingQueue small, but it costs time to remove fragments from the queue.
	private void removeFromLoadingQueue() {
		while (loadingQueue.remove(currentFragment)) {
			// noop
		}
	}
}
