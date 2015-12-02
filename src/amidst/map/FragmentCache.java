package amidst.map;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.logging.Log;

public class FragmentCache {
	private static final int NEW_FRAGMENTS_PER_REQUEST = 1024;

	private final List<Fragment> cache = new LinkedList<Fragment>();

	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final Iterable<FragmentConstructor> constructors;
	private final int numberOfLayers;

	public FragmentCache(ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			Iterable<FragmentConstructor> constructors, int numberOfLayers) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.constructors = constructors;
		this.numberOfLayers = numberOfLayers;
	}

	public void increaseSize() {
		Log.i("increasing fragment cache size from " + cache.size() + " to "
				+ (cache.size() + NEW_FRAGMENTS_PER_REQUEST));
		requestNewFragments();
		Log.i("fragment cache size increased to " + cache.size());
	}

	private void requestNewFragments() {
		for (int i = 0; i < NEW_FRAGMENTS_PER_REQUEST; i++) {
			Fragment fragment = new Fragment(numberOfLayers);
			construct(fragment);
			cache.add(fragment);
			availableQueue.offer(fragment);
		}
	}

	private void construct(Fragment fragment) {
		for (FragmentConstructor constructor : constructors) {
			constructor.construct(fragment);
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
