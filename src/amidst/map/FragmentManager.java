package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FragmentManager {
	private ConcurrentLinkedQueue<Fragment> fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> requestQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<Fragment>();

	private FragmentCache cache;
	private QueueProcessor queueProcessor;

	public FragmentManager(LayerContainer layerContainer) {
		this.cache = new FragmentCache(layerContainer, fragmentQueue);
		this.queueProcessor = new QueueProcessor(fragmentQueue, requestQueue,
				recycleQueue);
	}

	public Fragment requestFragment(int x, int y) {
		if (!queueProcessor.isRunning()) {
			return null;
		}
		Fragment fragment;
		while ((fragment = fragmentQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.initialize(x, y);
		requestQueue.offer(fragment);
		return fragment;
	}

	public void repaintFragmentImageLayers(Fragment fragment) {
		fragment.repaintAllImageLayers();
	}

	public void repaintFragmentImageLayer(Fragment fragment, int layerId) {
		fragment.repaintImageLayer(layerId);
	}

	public void recycleFragment(Fragment fragment) {
		recycleQueue.offer(fragment);
	}

	public void reset() {
		queueProcessor.gracefullyShutdownCurrentThread();
		clearAllQueues();
		cache.resetAllFragments();
	}

	public void start() {
		queueProcessor.startNewThread();
	}

	private void clearAllQueues() {
		recycleQueue.clear();
		requestQueue.clear();
		fragmentQueue.clear();
	}

	public int getFreeFragmentQueueSize() {
		return fragmentQueue.size();
	}

	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}

	public int getRequestQueueSize() {
		return requestQueue.size();
	}

	public int getCacheSize() {
		return cache.size();
	}
}
