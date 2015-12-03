package amidst.map;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.logging.Log;

public class FragmentCache {
	private static final int NEW_FRAGMENTS_PER_REQUEST = 1024;

	private final List<FragmentGraphItem> cache = new LinkedList<FragmentGraphItem>();

	private final ConcurrentLinkedQueue<FragmentGraphItem> availableQueue;
	private final ConcurrentLinkedQueue<FragmentGraphItem> loadingQueue;
	private final Iterable<FragmentConstructor> constructors;
	private final int numberOfLayers;

	public FragmentCache(ConcurrentLinkedQueue<FragmentGraphItem> availableQueue,
			ConcurrentLinkedQueue<FragmentGraphItem> loadingQueue,
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
			FragmentGraphItem fragment = new FragmentGraphItem(numberOfLayers);
			construct(fragment);
			cache.add(fragment);
			availableQueue.offer(fragment);
		}
	}

	private void construct(FragmentGraphItem fragment) {
		for (FragmentConstructor constructor : constructors) {
			constructor.construct(fragment);
		}
	}

	public void reloadAll() {
		for (FragmentGraphItem fragment : cache) {
			loadingQueue.offer(fragment);
		}
	}

	public int size() {
		return cache.size();
	}
}
