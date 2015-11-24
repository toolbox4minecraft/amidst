package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;

public class FragmentManager {
	private ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> resetQueue = new ConcurrentLinkedQueue<Fragment>();

	private FragmentCache cache;
	private FragmentLoader loader;

	public FragmentManager(LayerContainer layerContainer) {
		this.cache = new FragmentCache(layerContainer, availableQueue,
				loadingQueue, resetQueue);
		this.loader = new FragmentLoader(layerContainer, availableQueue,
				loadingQueue, resetQueue);
	}

	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.initialize(coordinates);
		loadingQueue.offer(fragment);
		return fragment;
	}

	/**
	 * Make sure the passed fragment is no longer referenced by other fragments!
	 */
	public void recycleFragment(Fragment fragment) {
		fragment.setNeedsReset();
		resetQueue.offer(fragment);
	}

	public void tick() {
		loader.tick();
	}

	public void reloadAll() {
		loadingQueue.clear();
		cache.reloadAll();
	}

	public void reset() {
		availableQueue.clear();
		loadingQueue.clear();
		resetQueue.clear();
		cache.resetAll();
	}

	public int getAvailableQueueSize() {
		return availableQueue.size();
	}

	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	public int getResetQueueSize() {
		return resetQueue.size();
	}

	public int getCacheSize() {
		return cache.size();
	}

	public void setWorld(World world) {
		loader.setWorld(world);
	}
}
