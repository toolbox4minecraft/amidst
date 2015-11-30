package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FragmentLoader {
	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> resetQueue;
	private final LayerContainer layerContainer;
	private final int[] imageCache = new int[Fragment.SIZE * Fragment.SIZE];

	private Fragment currentFragment;

	public FragmentLoader(ConcurrentLinkedQueue<Fragment> availableQueue,
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
				layerContainer.reloadInvalidated(currentFragment, imageCache);
			} else {
				currentFragment.prepareLoad();
				layerContainer.loadAll(currentFragment, imageCache);
				currentFragment.setLoaded(true);
			}
		}
	}

	private void resetFragment() {
		currentFragment.setLoaded(false);
		currentFragment.setInitialized(false);
		while (loadingQueue.remove(currentFragment)) {
			// noop
		}
		availableQueue.offer(currentFragment);
	}
}
