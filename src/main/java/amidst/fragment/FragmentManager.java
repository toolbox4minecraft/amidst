package amidst.fragment;

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
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<>();
	
	private final AvailableFragmentCache availableCache;
	private final OffScreenFragmentCache offscreenCache;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentManager(Iterable<FragmentConstructor> constructors, int numberOfLayers) {
		this.availableCache = new AvailableFragmentCache(constructors, numberOfLayers);
		this.offscreenCache = new OffScreenFragmentCache(recycleQueue);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void invalidateCaches() {
		offscreenCache.invalidate();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		// We get and remove the fragments that come on screen from the loading cache,
		// returning it if it exists.
		Fragment fragment = offscreenCache.remove(coordinates);
		if (fragment != null) {
			return fragment;
		}

		fragment = availableCache.getOrCreate();
		fragment.setCorner(coordinates);
		fragment.setInitialized();
		loadingQueue.offer(fragment);
		return fragment;
	}

	/**
	 * Called when a fragment is no longer shown on the screen.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public void retireFragment(Fragment fragment) {
		if (!fragment.isLoaded()) {
			// Send it back to the available cache
			fragment.recycle();
			loadingQueue.remove(fragment);
			availableCache.put(fragment);
		} else {
			// We store the fragment if it's loaded and it goes offscreen
			offscreenCache.put(fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentQueueProcessor createQueueProcessor(
			LayerManager layerManager,
			Setting<Dimension> dimensionSetting,
			FragmentGraph graph) {
		
		return new FragmentQueueProcessor(
				loadingQueue,
				recycleQueue,
				availableCache,
				offscreenCache,
				layerManager,
				dimensionSetting,
				graph);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getAvailableCacheSize() {
		return availableCache.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getOffscreenCacheSize() {
		return offscreenCache.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}
	
}
