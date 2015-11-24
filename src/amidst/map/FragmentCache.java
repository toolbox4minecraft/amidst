package amidst.map;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.logging.Log;
import amidst.minecraft.world.World;

public class FragmentCache {
	private static final int NEW_FRAGMENTS_PER_REQUEST = 1024;

	private final ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> resetQueue = new ConcurrentLinkedQueue<Fragment>();
	private final List<Fragment> cache = new LinkedList<Fragment>();
	private final FragmentFactory fragmentFactory;
	private final LayerContainerFactory layerContainerFactory;

	public FragmentCache(FragmentFactory factory,
			LayerContainerFactory layerContainerFactory) {
		this.fragmentFactory = factory;
		this.layerContainerFactory = layerContainerFactory;
		requestNewFragments();
	}

	private void requestNewFragments() {
		for (int i = 0; i < NEW_FRAGMENTS_PER_REQUEST; i++) {
			Fragment fragment = fragmentFactory.create();
			cache.add(fragment);
			availableQueue.offer(fragment);
		}
	}

	public void increaseSize() {
		Log.i("increasing fragment cache size from " + cache.size() + " to "
				+ (cache.size() + NEW_FRAGMENTS_PER_REQUEST));
		requestNewFragments();
		Log.i("fragment cache size increased to " + cache.size());
	}

	public void resetAll() {
		for (Fragment fragment : cache) {
			fragment.setNeedsReset();
			resetQueue.offer(fragment);
		}
	}

	public void reloadAll() {
		for (Fragment fragment : cache) {
			loadingQueue.offer(fragment);
		}
	}

	public int size() {
		return cache.size();
	}

	public FragmentManager createFragmentManager(World world, Map map) {
		LayerContainer layerContainer = layerContainerFactory
				.create(world, map);
		FragmentLoader fragmentLoader = new FragmentLoader(availableQueue,
				loadingQueue, resetQueue, layerContainer);
		return new FragmentManager(availableQueue, loadingQueue, resetQueue,
				this, fragmentLoader, layerContainer);
	}
}
