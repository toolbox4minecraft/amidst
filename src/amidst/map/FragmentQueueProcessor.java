package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FragmentQueueProcessor {
	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> resetQueue;
	private final LayerContainer layerContainer;

	private Fragment currentFragment;

	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> resetQueue,
			LayerContainer layerContainer) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.resetQueue = resetQueue;
		this.layerContainer = layerContainer;
	}

	public void tick() {
		processResetQueue();
		while ((currentFragment = loadingQueue.poll()) != null) {
			loadFragment();
			processResetQueue();
		}
		layerContainer.clearInvalidatedLayers();
	}

	private void processResetQueue() {
		while ((currentFragment = resetQueue.poll()) != null) {
			resetFragment();
		}
	}

	private void loadFragment() {
		if (currentFragment.isInitialized()) {
			if (currentFragment.isLoaded()) {
				currentFragment.prepareReload();
				layerContainer.reloadInvalidated(currentFragment);
			} else {
				currentFragment.prepareLoad();
				layerContainer.loadAll(currentFragment);
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
