package amidst.map;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
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
			currentFragment.clearInvalidatedImageLayers();
			currentFragment.clearInvalidatedIconLayers();
			loadAllImages();
			loadAllIconLayers();
			currentFragment.initAlpha();
			currentFragment.setLoaded();
		} else if (currentFragment.isLoaded()) {
			reloadInvalidatedImages();
			reloadInvalidatedIconLayers();
			currentFragment.clearInvalidatedImageLayers();
			currentFragment.clearInvalidatedIconLayers();
		}
	}

	private void loadAllImages() {
		for (ImageLayer imageLayer : layerContainer.getImageLayers()) {
			loadImage(imageLayer.getLayerType());
		}
	}

	private void loadAllIconLayers() {
		for (IconLayer iconLayer : layerContainer.getIconLayers()) {
			loadIconLayer(iconLayer.getLayerType());
		}
	}

	private void reloadInvalidatedImages() {
		for (ImageLayer imageLayer : currentFragment
				.getInvalidatedImageLayers()) {
			loadImage(imageLayer.getLayerType());
		}
	}

	private void reloadInvalidatedIconLayers() {
		for (IconLayer iconLayer : currentFragment.getInvalidatedIconLayers()) {
			removeIconLayer(iconLayer.getLayerType());
			loadIconLayer(iconLayer.getLayerType());
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

	private void loadImage(LayerType layerType) {
		ImageLayer imageLayer = layerContainer.getImageLayer(layerType);
		imageLayer.drawToCache(currentFragment, imageCache,
				currentFragment.getImage(imageLayer.getLayerId()));
	}

	private void loadIconLayer(LayerType layerType) {
		IconLayer iconLayer = layerContainer.getIconLayer(layerType);
		iconLayer.generateMapObjects(currentFragment);
	}
}
