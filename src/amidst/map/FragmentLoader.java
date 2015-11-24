package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.map.layer.LayerType;

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
		layerContainer.clearInvalidatedLayerTypes();
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
		for (LayerType layerType : layerContainer.getAllLayerTypes()) {
			loadLayer(layerType);
		}
	}

	private void reloadInvalidatedLayers() {
		for (LayerType layerType : layerContainer.getInvalidatedLayerTypes()) {
			reloadLayer(layerType);
		}
	}

	private void loadLayer(LayerType layerType) {
		layerContainer.getLayer(layerType).load(currentFragment, imageCache);
	}

	private void reloadLayer(LayerType layerType) {
		layerContainer.getLayer(layerType).reload(currentFragment, imageCache);
	}
}
