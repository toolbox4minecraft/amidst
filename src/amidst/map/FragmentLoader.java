package amidst.map;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;
import amidst.map.layer.MapObject;

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
			currentFragment.clearMapObjects();
			loadAllLayers();
			currentFragment.initAlpha();
			currentFragment.setLoaded();
		} else if (currentFragment.isLoaded()) {
			reloadInvalidatedLayers();
		}
	}

	private void loadAllLayers() {
		for (LayerType layerType : layerContainer.getLoadableLayerTypes()) {
			loadLayer(layerType);
		}
	}

	private void reloadInvalidatedLayers() {
		for (LayerType layerType : layerContainer.getInvalidatedLayerTypes()) {
			if (layerContainer.isIconLayer(layerType)) {
				removeIconLayer(layerType);
			}
			loadLayer(layerType);
		}
	}

	private void removeIconLayer(LayerType layerType) {
		List<MapObject> objectsToRemove = new LinkedList<MapObject>();
		for (MapObject mapObject : currentFragment.getMapObjects()) {
			if (mapObject.getIconLayer().getLayerType() == layerType) {
				objectsToRemove.add(mapObject);
			}
		}
		currentFragment.getMapObjects().removeAll(objectsToRemove);
	}

	private void loadLayer(LayerType layerType) {
		Layer layer = layerContainer.getLayer(layerType);
		if (layer instanceof ImageLayer) {
			loadImageLayer((ImageLayer) layer);
		} else if (layer instanceof IconLayer) {
			loadIconLayer((IconLayer) layer);
		}
	}

	private void loadImageLayer(ImageLayer imageLayer) {
		imageLayer.drawToCache(currentFragment, imageCache,
				currentFragment.getImage(imageLayer.getLayerType()));
	}

	private void loadIconLayer(IconLayer iconLayer) {
		iconLayer.generateMapObjects(currentFragment);
	}
}
