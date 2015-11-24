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
			loadImage(imageLayer);
		}
	}

	private void loadAllIconLayers() {
		for (IconLayer iconLayer : layerContainer.getIconLayers()) {
			loadIconLayer(iconLayer);
		}
	}

	private void reloadInvalidatedImages() {
		for (ImageLayer imageLayer : currentFragment
				.getInvalidatedImageLayers()) {
			loadImage(imageLayer);
		}
	}

	private void reloadInvalidatedIconLayers() {
		for (IconLayer iconLayer : currentFragment.getInvalidatedIconLayers()) {
			removeIconLayer(iconLayer);
			loadIconLayer(iconLayer);
		}
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

	private void loadImage(ImageLayer imageLayer) {
		imageLayer.drawToCache(currentFragment, imageCache,
				currentFragment.getImage(imageLayer.getLayerId()));
	}

	private void loadIconLayer(IconLayer iconLayer) {
		iconLayer.generateMapObjects(currentFragment);
	}
}
