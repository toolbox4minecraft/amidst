package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.map.layer.LayerType;
import amidst.minecraft.world.World;

public class FragmentLoader {
	private LayerContainer layerContainer;
	private ConcurrentLinkedQueue<Fragment> availableQueue;
	private ConcurrentLinkedQueue<Fragment> loadingQueue;
	private ConcurrentLinkedQueue<Fragment> resetQueue;

	private int[] imageCache = new int[Fragment.SIZE * Fragment.SIZE];
	private Fragment currentFragment;
	private World world;

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
			currentFragment.clearMapObjects();
			world.populateBiomeDataArray(currentFragment);
			loadAllLayers();
			currentFragment.initAlpha();
			currentFragment.setLoaded();
		} else if (currentFragment.isLoaded()) {
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
			removeIconLayer(layerType);
			loadLayer(layerType);
		}
	}

	private void removeIconLayer(LayerType layerType) {
		currentFragment.removeMapObjects(layerType);
	}

	private void loadLayer(LayerType layerType) {
		layerContainer.getLayer(layerType).load(currentFragment, imageCache);
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
