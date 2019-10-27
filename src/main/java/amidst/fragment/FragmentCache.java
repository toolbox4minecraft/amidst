package amidst.fragment;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.logging.AmidstLogger;

@ThreadSafe
public class FragmentCache {
	private static final int NEW_FRAGMENTS_PER_REQUEST = 1024;

	private final List<Fragment> cache = new LinkedList<>();
	private volatile int cacheSize = 0;

	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final Iterable<FragmentConstructor> constructors;
	private final int numberOfLayers;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentCache(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			Iterable<FragmentConstructor> constructors,
			int numberOfLayers) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.constructors = constructors;
		this.numberOfLayers = numberOfLayers;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public synchronized void increaseSize() {
		AmidstLogger.info(
				"increasing fragment cache size from " + cache.size() + " to "
						+ (cache.size() + NEW_FRAGMENTS_PER_REQUEST));
		requestNewFragments();
		AmidstLogger.info("fragment cache size increased to " + cache.size());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void requestNewFragments() {
		for (int i = 0; i < NEW_FRAGMENTS_PER_REQUEST; i++) {
			Fragment fragment = new Fragment(numberOfLayers);
			construct(fragment);
			cache.add(fragment);
			availableQueue.offer(fragment);
		}
		cacheSize = cache.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void construct(Fragment fragment) {
		for (FragmentConstructor constructor : constructors) {
			constructor.construct(fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public synchronized void reloadAll() {
		loadingQueue.clear();
		for (Fragment fragment : cache) {
			loadingQueue.offer(fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int size() {
		return cacheSize;
	}
}
