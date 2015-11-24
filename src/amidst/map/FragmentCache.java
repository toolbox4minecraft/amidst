package amidst.map;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.logging.Log;
import amidst.map.layer.ImageLayer;

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
		ImageLayer[] imageLayers = layerContainer.getImageLayers();
		BufferedImage[] images = new BufferedImage[imageLayers.length];
		boolean[] repaintImage = new boolean[imageLayers.length];
		for (ImageLayer imageLayer : imageLayers) {
			int layerId = imageLayer.getLayerId();
			int layerSize = imageLayer.getSize();
			images[layerId] = new BufferedImage(layerSize, layerSize,
					BufferedImage.TYPE_INT_ARGB);
			repaintImage[layerId] = true;
		}
		return new Fragment(images, repaintImage);
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
