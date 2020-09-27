package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedDeque;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.settings.Setting;
import amidst.util.SelfExpiringSoftHashMap;

/**
 * This contains fragments that have been loaded and are currently off-screen.
 */
@NotThreadSafe
public class OffScreenFragmentCache {	
	private final SelfExpiringSoftHashMap<CoordinatesInWorld, Fragment> cache = new SelfExpiringSoftHashMap<>();
	private final ConcurrentLinkedDeque<Fragment> recycleQueue;
	private final Setting<Integer> offscreenCacheTime;

	public OffScreenFragmentCache(ConcurrentLinkedDeque<Fragment> recycleQueue, Setting<Integer> offscreenCacheTime) {
		this.recycleQueue = recycleQueue;
		this.offscreenCacheTime = offscreenCacheTime;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment get(CoordinatesInWorld coordinates) {
		return cache.get(coordinates);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void put(Fragment fragment) {
		if (fragment != null) {
			Fragment frag = get(fragment.getCorner());
	        if (frag == null) {
	        	cache.put(fragment.getCorner(), fragment, offscreenCacheTime.get());
	        }
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment remove(CoordinatesInWorld coordinates) {
		return cache.remove(coordinates);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void invalidate() {
		// When we invalidate the cache, we can recycle the fragments
		recycleQueue.addAll(cache.values());
		clear();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void clear() {
		cache.clear();
	}

	public void clean() {
		cache.clean();
	}

	public void cleanAndRecycle() {
		cache.clean(f -> recycleQueue.add(f));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int size() {
		return cache.size();
	}
}
