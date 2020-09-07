package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedDeque;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.util.SelfExpiringSoftHashMap;

/**
 * This contains fragments that have been loaded and are currently off-screen.
 */
@NotThreadSafe
public class OffScreenFragmentCache {
	private static final long EXPIRATION_MILLIS = 30000; // 30 seconds
	
	private final SelfExpiringSoftHashMap<CoordinatesInWorld, Fragment> cache = new SelfExpiringSoftHashMap<>(EXPIRATION_MILLIS);
	private final ConcurrentLinkedDeque<Fragment> recycleQueue;

	public OffScreenFragmentCache(ConcurrentLinkedDeque<Fragment> recycleQueue) {
		this.recycleQueue = recycleQueue;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment get(CoordinatesInWorld coordinates) {
		return cache.get(coordinates);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void put(Fragment fragment) {
		if (fragment != null) {
			cache.putIfAbsent(fragment.getCorner(), fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment remove(CoordinatesInWorld coordinates) {
		return cache.remove(coordinates);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void invalidate() {
		// When we invalidate the cache, we can recycle the fragments.
		// (This recycling is probably neither necessary nor efficient, but let's keep it for now)
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

	@CalledOnlyBy(AmidstThread.EDT)
	public int size() {
		return cache.size();
	}
}
