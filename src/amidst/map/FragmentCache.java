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
	private final LayerContainerFactory layerContainerFactory;
	private LayerContainer layerContainer;

	public FragmentCache(LayerContainerFactory layerContainerFactory) {
		this.layerContainerFactory = layerContainerFactory;
	}

	public void increaseSize() {
		Log.i("increasing fragment cache size from " + cache.size() + " to "
				+ (cache.size() + NEW_FRAGMENTS_PER_REQUEST));
		requestNewFragments();
		Log.i("fragment cache size increased to " + cache.size());
	}

	private void requestNewFragments() {
		for (int i = 0; i < NEW_FRAGMENTS_PER_REQUEST; i++) {
			Fragment fragment = new Fragment();
			layerContainer.constructAll(fragment);
			cache.add(fragment);
			availableQueue.offer(fragment);
		}
	}

	public void resetAll() {
		for (Fragment fragment : cache) {
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
		layerContainer = layerContainerFactory.createLayerContainer(world, map);
		FragmentQueueProcessor fragmentLoader = new FragmentQueueProcessor(
				availableQueue, loadingQueue, resetQueue, layerContainer);
		return new FragmentManager(availableQueue, loadingQueue, resetQueue,
				this, fragmentLoader, layerContainer);
	}
}
