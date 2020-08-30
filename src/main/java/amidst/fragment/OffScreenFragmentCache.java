package amidst.fragment;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

/**
 * This contains fragments that have been loaded and are currently off-screen.
 */
public class OffScreenFragmentCache {
	private final Map<CoordinatesInWorld, SoftReference<Fragment>> cache = new HashMap<>();
	private final ConcurrentLinkedQueue<Fragment> recycleQueue;

	public OffScreenFragmentCache(ConcurrentLinkedQueue<Fragment> recycleQueue) {
		this.recycleQueue = recycleQueue;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment get(CoordinatesInWorld coordinates) {
		SoftReference<Fragment> softref = cache.get(coordinates);
		if (softref != null) {
			return softref.get();
		} else {
			// No cache hit
			cache.remove(coordinates, softref);
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void put(Fragment fragment) {
		if (fragment != null) {
			cache.putIfAbsent(fragment.getCorner(), new SoftReference<Fragment>(fragment));
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment remove(CoordinatesInWorld coordinates) {
		SoftReference<Fragment> ref = cache.remove(coordinates);
		return ref == null ? null : ref.get();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void invalidate() {
		// When we invalidate the cache, we can recycle the fragments.
		// (This recycling is probably neither necessary nor efficient, but let's keep it for now)
		recycleQueue.addAll(cache.values().stream()
				.map(softref -> softref.get()).filter(fragment -> fragment != null)
				.collect(Collectors.toList()));
		clear();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void clear() {
		cache.clear();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public synchronized void clean() {
		Iterator<Entry<CoordinatesInWorld, SoftReference<Fragment>>> entrySet = cache.entrySet().iterator();
		
		for(Entry<CoordinatesInWorld, SoftReference<Fragment>> entry : (Iterable<Entry<CoordinatesInWorld, SoftReference<Fragment>>>) () -> entrySet) {
			if(entry.getValue().get() == null) {
				entrySet.remove();
			}
		}
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	public int size() {
		clean();
		return cache.size();
	}
}
