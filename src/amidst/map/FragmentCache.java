package amidst.map;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.logging.Log;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.LayerType;

public class FragmentCache {
	private static final int NEW_FRAGMENTS_PER_REQUEST = 1024;

	private List<Fragment> cache = new LinkedList<Fragment>();
	private LayerContainer layerContainer;
	private ConcurrentLinkedQueue<Fragment> availableQueue;
	private ConcurrentLinkedQueue<Fragment> loadingQueue;
	private ConcurrentLinkedQueue<Fragment> resetQueue;

	public FragmentCache(LayerContainer layerContainer,
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> resetQueue) {
		this.layerContainer = layerContainer;
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.resetQueue = resetQueue;
		requestNewFragments();
	}

	private void requestNewFragments() {
		for (int i = 0; i < NEW_FRAGMENTS_PER_REQUEST; i++) {
			Fragment fragment = createFragment();
			cache.add(fragment);
			availableQueue.offer(fragment);
		}
	}

	private Fragment createFragment() {
		EnumMap<LayerType, BufferedImage> images = new EnumMap<LayerType, BufferedImage>(
				LayerType.class);
		for (ImageLayer imageLayer : layerContainer.getImageLayers()) {
			images.put(imageLayer.getLayerType(),
					createBufferedImage(imageLayer.getSize()));
		}
		return new Fragment(images);
	}

	private BufferedImage createBufferedImage(int layerSize) {
		return new BufferedImage(layerSize, layerSize,
				BufferedImage.TYPE_INT_ARGB);
	}

	public void increaseSize() {
		Log.i("increasing fragment cache size from " + cache.size() + " to "
				+ (cache.size() + NEW_FRAGMENTS_PER_REQUEST));
		requestNewFragments();
		Log.i("fragment cache size increased to " + cache.size());
	}

	public void resetAll() {
		for (Fragment fragment : cache) {
			fragment.setNeedsReset();
			resetQueue.offer(fragment);
		}
	}

	public void reloadAll() {
		for (Fragment fragment : cache) {
			loadingQueue.offer(fragment);
		}
	}

	public int size() {
		return cache.size();
	}
}
