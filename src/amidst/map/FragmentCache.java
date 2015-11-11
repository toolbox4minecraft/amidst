package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.logging.Log;

public class FragmentCache {
	private static final int INITIAL_CACHE_SIZE = 1024;

	private Fragment[] cache = new Fragment[INITIAL_CACHE_SIZE];
	private LayerContainer layerContainer;
	private ConcurrentLinkedQueue<Fragment> fragmentQueue;

	public FragmentCache(LayerContainer layerContainer,
			ConcurrentLinkedQueue<Fragment> fragmentQueue) {
		this.layerContainer = layerContainer;
		this.fragmentQueue = fragmentQueue;
		this.cache = initWithFragments(cache, 0, cache.length);
	}

	private Fragment[] initWithFragments(Fragment[] cache, int from, int to) {
		for (int i = from; i < to; i++) {
			cache[i] = new Fragment(layerContainer);
			fragmentQueue.offer(cache[i]);
		}
		return cache;
	}

	public void doubleSize() {
		int currentSize = cache.length;
		int newSize = currentSize << 1;
		Fragment[] newFragmentCache = newFromOldFragmentCache(newSize);
		cache = initWithFragments(newFragmentCache, currentSize, newSize);
		Log.i("FragmentCache size increased from " + currentSize + " to "
				+ newSize);

		// TODO: do we really need to run the gc manually?
		System.gc();
	}

	private Fragment[] newFromOldFragmentCache(int newSize) {
		Fragment[] newCache = new Fragment[newSize];
		System.arraycopy(this.cache, 0, newCache, 0, this.cache.length);
		return newCache;
	}

	public void resetAllFragments() {
		for (int i = 0; i < cache.length; i++) {
			cache[i].reset();
			fragmentQueue.offer(cache[i]);
		}
	}

	public int size() {
		return cache.length;
	}
}
