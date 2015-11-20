package amidst.map;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.logging.Log;

public class FragmentCache {
	private static final int NEW_FRAGMENTS_PER_REQUEST = 1024;

	private List<Fragment> cache = new LinkedList<Fragment>();
	private LayerContainer layerContainer;
	private ConcurrentLinkedQueue<Fragment> fragmentQueue;
	private ConcurrentLinkedQueue<Fragment> requestQueue;

	public FragmentCache(LayerContainer layerContainer,
			ConcurrentLinkedQueue<Fragment> fragmentQueue,
			ConcurrentLinkedQueue<Fragment> requestQueue) {
		this.layerContainer = layerContainer;
		this.fragmentQueue = fragmentQueue;
		this.requestQueue = requestQueue;
		requestNewFragments();
	}

	private void requestNewFragments() {
		for (int i = 0; i < NEW_FRAGMENTS_PER_REQUEST; i++) {
			Fragment fragment = new Fragment(layerContainer);
			cache.add(fragment);
			fragmentQueue.offer(fragment);
		}
	}

	public void increaseSize() {
		Log.i("increasing fragment cache size from " + cache.size() + " to "
				+ (cache.size() + NEW_FRAGMENTS_PER_REQUEST));
		requestNewFragments();
		Log.i("fragment cache size increased to " + cache.size());
	}

	public void resetAll() {
		for (Fragment fragment : cache) {
			fragment.reset();
			fragmentQueue.offer(fragment);
		}
	}

	public void reloadAll() {
		for (Fragment fragment : cache) {
			requestQueue.offer(fragment);
		}
	}

	public int size() {
		return cache.size();
	}
}
