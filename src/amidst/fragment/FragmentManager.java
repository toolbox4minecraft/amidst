package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledBy;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.layer.LayerLoader;
import amidst.mojangapi.world.CoordinatesInWorld;

@NotThreadSafe
public class FragmentManager {
	private final ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<Fragment>();
	private final FragmentCache cache;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentManager(Iterable<FragmentConstructor> constructors,
			int numberOfLayers) {
		this.cache = new FragmentCache(availableQueue, loadingQueue,
				constructors, numberOfLayers);
	}

	@CalledBy(AmidstThread.EDT)
	@CalledByAny
	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.setCorner(coordinates);
		fragment.setInitialized();
		loadingQueue.offer(fragment);
		return fragment;
	}

	@CalledBy(AmidstThread.EDT)
	@CalledByAny
	public void recycleFragment(Fragment fragment) {
		recycleQueue.offer(fragment);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentQueueProcessor createLayerLoader(LayerLoader layerLoader) {
		return new FragmentQueueProcessor(availableQueue, loadingQueue,
				recycleQueue, cache, layerLoader);
	}

	@CalledByAny
	public int getAvailableQueueSize() {
		return availableQueue.size();
	}

	@CalledByAny
	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	@CalledByAny
	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}

	@CalledByAny
	public int getCacheSize() {
		return cache.size();
	}
}
