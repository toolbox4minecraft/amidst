package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.layer.LayerManager;

public class FragmentQueueProcessor {
	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> resetQueue;
	private final LayerManager layerManager;

	private Fragment currentFragment;

	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> resetQueue,
			LayerManager layerManager) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.resetQueue = resetQueue;
		this.layerManager = layerManager;
	}

	/**
	 * This method might be called from any thread.
	 */
	public void invalidateLayer(int layerId) {
		layerManager.invalidateLayer(layerId);
	}

	/**
	 * This method is only called from the fragment loading thread.
	 */
	public void tick() {
		processResetQueue();
		while ((currentFragment = loadingQueue.poll()) != null) {
			loadFragment();
			processResetQueue();
		}
		layerManager.clearInvalidatedLayers();
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
