package amidst.fragment;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.layer.LayerManager;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.settings.Setting;

@NotThreadSafe
public class FragmentManager {
	private final ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedDeque<Fragment> backgroundQueue =  new ConcurrentLinkedDeque<>();
	private final ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<>();
	private final FragmentCache cache;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentManager(Iterable<FragmentConstructor> constructors, int numberOfLayers) {
		this.cache = new FragmentCache(availableQueue, loadingQueue, constructors, numberOfLayers);
	}

	private final static Map<CoordinatesInWorld, Fragment> fragmentCache = new WeakHashMap<>();
	private Fragment getFragmentFromCache(CoordinatesInWorld coordinates) {
		Fragment fragment = fragmentCache.get(coordinates);
		if (fragment != null && !fragment.isLoaded()) {
			// It has not finished loading, request high priority loading
			loadingQueue.offer(fragment);
			backgroundQueue.remove(fragment);
		}
		return fragment;
	}

	private void storeFragmentInCache(Fragment fragment, CoordinatesInWorld coordinates) {
		fragmentCache.putIfAbsent(coordinates, fragment);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment = getFragmentFromCache(coordinates);
		if (fragment != null) {
			return fragment;
		}
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.setCorner(coordinates);
		fragment.setInitialized();
		storeFragmentInCache(fragment, coordinates);
		loadingQueue.offer(fragment);
		return fragment;
	}

	/**
	 * Called when a fragment is no longer shown on the screen.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public void retireFragment(Fragment fragment) {
		if (!fragment.isLoaded()) {
			// Make rendering of this fragment lowest effort
			loadingQueue.remove(fragment);
			// Assume last unloaded is most likely to be reloaded, so put it first in the queue
			backgroundQueue.offerFirst(fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentQueueProcessor createQueueProcessor(LayerManager layerManager, Setting<Dimension> dimensionSetting) {
		return new FragmentQueueProcessor(
				availableQueue,
				loadingQueue,
				backgroundQueue,
				recycleQueue,
				cache,
				layerManager,
				dimensionSetting);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getAvailableQueueSize() {
		return availableQueue.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getCacheSize() {
		return cache.size();
	}
}
