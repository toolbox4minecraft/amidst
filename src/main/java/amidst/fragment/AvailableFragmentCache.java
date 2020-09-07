package amidst.fragment;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.constructor.FragmentConstructor;

@NotThreadSafe
public class AvailableFragmentCache {
	private final ConcurrentLinkedQueue<SoftReference<Fragment>> cache = new ConcurrentLinkedQueue<>();
	
	private final Iterable<FragmentConstructor> constructors;
	private final int numberOfLayers;

	@CalledOnlyBy(AmidstThread.EDT)
	public AvailableFragmentCache(
			Iterable<FragmentConstructor> constructors,
			int numberOfLayers) {
		this.constructors = constructors;
		this.numberOfLayers = numberOfLayers;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment getOrCreate() {
		Fragment fragment;
		if((fragment = poll()) == null) {
			fragment = new Fragment(numberOfLayers);
			construct(fragment);
		}
		
		return fragment;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Fragment poll() {
		SoftReference<Fragment> ref = (SoftReference<Fragment>) cache.poll();
		return ref != null ? ref.get() : null;
	}

	/**
	 * Fragments that are used when calling this method should already
	 * be recycled.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public void put(Fragment fragment) {
		cache.offer(new SoftReference<>(fragment));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void construct(Fragment fragment) {
		for (FragmentConstructor constructor : constructors) {
			constructor.construct(fragment);
		}
	}

	public void clean() {
		Iterator<SoftReference<Fragment>> fragRefIterator = cache.iterator();
		for (SoftReference<Fragment> fragRef : (Iterable<SoftReference<Fragment>>) () -> fragRefIterator) {
			if (fragRef == null || fragRef.get() == null) {
				fragRefIterator.remove();
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int size() {
		return cache.size();
	}
}
