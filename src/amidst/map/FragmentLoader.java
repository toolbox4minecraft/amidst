package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.map.layer.Layer;

public class FragmentLoader {
	private LayerContainer layerContainer;
	private ConcurrentLinkedQueue<Fragment> availableQueue;
	private ConcurrentLinkedQueue<Fragment> loadingQueue;
	private ConcurrentLinkedQueue<Fragment> resetQueue;

	private int[] imageCache = new int[Fragment.SIZE * Fragment.SIZE];
	private Fragment currentFragment;

	public FragmentLoader(LayerContainer layerContainer,
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> resetQueue) {
		this.layerContainer = layerContainer;
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.resetQueue = resetQueue;
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
			loadFragment();
		}
	}

	private void loadFragment() {
		if (currentFragment.needsReset()) {
			currentFragment.setAvailable();
			availableQueue.offer(currentFragment);
		} else if (currentFragment.isInitialized()) {
			currentFragment.prepareLoad();
			loadAllLayers();
			currentFragment.setLoaded();
		} else if (currentFragment.isLoaded()) {
			currentFragment.prepareReload();
			reloadInvalidatedLayers();
		}
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
