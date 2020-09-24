package amidst.fragment;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.util.SoftExpiringReference;

@NotThreadSafe
public class AvailableFragmentCache {
	private static final long EXPIRATION_MILLIS = 60000; // 1 minute
	
	private final ConcurrentLinkedDeque<SoftExpiringReference<Fragment>> cache = new ConcurrentLinkedDeque<>();
	
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
		Fragment fragment = null;
		while(!isEmpty() && (fragment = poll()) == null); // try to retrieve a non-null fragment from the cache
		
		if(fragment == null) {
			fragment = new Fragment(numberOfLayers);
			construct(fragment);
		}
		
		return fragment;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Fragment poll() {
		SoftExpiringReference<Fragment> value = (SoftExpiringReference<Fragment>) cache.pollFirst();
		return value != null ? value.getValue() : null;
	}

	/**
	 * Fragments that are used when calling this method should already
	 * be recycled.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public void put(Fragment fragment) {
		cache.addLast(new SoftExpiringReference<>(fragment, EXPIRATION_MILLIS));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void construct(Fragment fragment) {
		for (FragmentConstructor constructor : constructors) {
			constructor.construct(fragment);
		}
	}

	public synchronized void clean() {
		Iterator<SoftExpiringReference<Fragment>> fragRefIterator = cache.iterator();
		for (SoftExpiringReference<Fragment> fragRef : (Iterable<SoftExpiringReference<Fragment>>) () -> fragRefIterator) {
			if (fragRef == null || fragRef.getValue() == null || fragRef.getDelayMillis() < 0) {
				fragRefIterator.remove();
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int size() {
		return cache.size();
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isEmpty() {
		return cache.size() <= 0;
	}
}
