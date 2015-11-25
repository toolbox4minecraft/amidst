package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.map.layer.Layer;

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
				reloadInvalidatedLayers();
			} else {
				currentFragment.prepareLoad();
				loadAllLayers();
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

	private void loadAllLayers() {
		for (Layer layer : layerContainer.getAllLayers()) {
			loadLayer(layer);
		}
	}

	private void reloadInvalidatedLayers() {
		for (Layer layer : layerContainer.getInvalidatedLayers()) {
			reloadLayer(layer);
		}
	}

	private void loadLayer(Layer layer) {
		layer.load(currentFragment, imageCache);
	}

	private void reloadLayer(Layer layer) {
		layer.reload(currentFragment, imageCache);
	}
}
