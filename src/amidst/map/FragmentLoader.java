package amidst.map;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.MapObject;

public class FragmentLoader {
	private LayerContainer layerContainer;
	private ConcurrentLinkedQueue<Fragment> availableQueue;
	private ConcurrentLinkedQueue<Fragment> loadingQueue;

	private int[] imageCache = new int[Fragment.SIZE * Fragment.SIZE];
	private Fragment currentFragment;

	public FragmentLoader(LayerContainer layerContainer,
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue) {
		this.layerContainer = layerContainer;
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
	}

	public void processRequestQueue() {
		while (!loadingQueue.isEmpty()) {
			currentFragment = loadingQueue.poll();
			loadFragment();
		}
	}

	private void loadFragment() {
		if (currentFragment.needsReset()) {
			currentFragment.setAvailable();
			availableQueue.offer(currentFragment);
		} else if (currentFragment.isInitialized()) {
			currentFragment.clearMapObjects();
			currentFragment.clearInvalidatedIconLayers();
			repaintAllImages(layerContainer.getImageLayers());
			generateMapObjects();
			currentFragment.initAlpha();
			currentFragment.setLoaded();
		} else if (currentFragment.isLoaded()) {
			repaintInvalidatedImages(layerContainer.getImageLayers());
			reloadInvalidatedIconLayers();
		}
	}

	private void repaintInvalidatedImages(ImageLayer[] imageLayers) {
		for (int i = 0; i < imageLayers.length; i++) {
			if (currentFragment.needsImageRepaint(i)) {
				repaintImage(i, imageLayers);
			}
		}
	}

	private void reloadInvalidatedIconLayers() {
		for (IconLayer iconLayer : currentFragment.getInvalidatedIconLayers()) {
			removeIconLayer(iconLayer);
			iconLayer.generateMapObjects(currentFragment);
		}
		currentFragment.getInvalidatedIconLayers().clear();
	}

	private void removeIconLayer(IconLayer iconLayer) {
		List<MapObject> objectsToRemove = new LinkedList<MapObject>();
		for (MapObject mapObject : currentFragment.getMapObjects()) {
			if (mapObject.getIconLayer() == iconLayer) {
				objectsToRemove.add(mapObject);
			}
		}
		currentFragment.getMapObjects().removeAll(objectsToRemove);
	}

	private void repaintAllImages(ImageLayer[] imageLayers) {
		for (int i = 0; i < imageLayers.length; i++) {
			repaintImage(i, imageLayers);
		}
	}

	private void repaintImage(int layerId, ImageLayer[] imageLayers) {
		imageLayers[layerId].drawToCache(currentFragment, imageCache,
				currentFragment.getImage(layerId));
		currentFragment.setNeedsImageRepaint(layerId, false);
	}

	private void generateMapObjects() {
		for (IconLayer iconLayer : layerContainer.getIconLayers()) {
			iconLayer.generateMapObjects(currentFragment);
		}
	}
}
