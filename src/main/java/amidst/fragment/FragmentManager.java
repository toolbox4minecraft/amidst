package amidst.fragment;

import java.util.HashMap;
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
	private final ConcurrentLinkedDeque<Fragment> loadingQueue = new ConcurrentLinkedDeque<>();
	private final ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<>();
	private final FragmentCache cache;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentManager(Iterable<FragmentConstructor> constructors, int numberOfLayers) {
		this.cache = new FragmentCache(availableQueue, loadingQueue, constructors, numberOfLayers);
	}

	private final static Map<CoordinatesInWorld, Fragment> fragmentCache = new WeakHashMap<>();
	private void storeFragmentInCache(Fragment fragment, CoordinatesInWorld coordinates) {
		fragmentCache.putIfAbsent(coordinates, fragment);
	}
	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;

		fragment = fragmentCache.get(coordinates);
		if (fragment != null) {
			System.out.println("CACHE HIT!!!");
			return fragment;
		}
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.setCorner(coordinates);
		fragment.setInitialized();
		loadingQueue.offerFirst(fragment);
		storeFragmentInCache(fragment, coordinates);
		return fragment;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void recycleFragment(Fragment fragment) {
		recycleQueue.offer(fragment);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentQueueProcessor createQueueProcessor(LayerManager layerManager, Setting<Dimension> dimensionSetting) {
		return new FragmentQueueProcessor(
				availableQueue,
				loadingQueue,
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
