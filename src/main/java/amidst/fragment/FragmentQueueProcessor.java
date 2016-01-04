package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerLoader;
import amidst.threading.TaskQueue;

@NotThreadSafe
public class FragmentQueueProcessor {
	private final TaskQueue taskQueue = new TaskQueue();

	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> recycleQueue;
	private final FragmentCache cache;
	private final LayerLoader layerLoader;

	@CalledByAny
	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> recycleQueue, FragmentCache cache,
			LayerLoader layerLoader) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.recycleQueue = recycleQueue;
		this.cache = cache;
		this.layerLoader = layerLoader;
	}

	@CalledByAny
	public void invalidateLayer(final int layerId) {
		taskQueue.invoke(new Runnable() {
			@Override
			public void run() {
				doInvalidateLayer(layerId);
			}
		});
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void doInvalidateLayer(int layerId) {
		layerLoader.invalidateLayer(layerId);
		cache.reloadAll();
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void processQueues() {
		taskQueue.processTasks();
		processRecycleQueue();
		Fragment fragment;
		while ((fragment = loadingQueue.poll()) != null) {
			loadFragment(fragment);
			taskQueue.processTasks();
			processRecycleQueue();
		}
		layerLoader.clearInvalidatedLayers();
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void processRecycleQueue() {
		Fragment fragment;
		while ((fragment = recycleQueue.poll()) != null) {
			recycleFragment(fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void loadFragment(Fragment fragment) {
		if (fragment.isInitialized()) {
			if (fragment.isLoaded()) {
				layerLoader.reloadInvalidated(fragment);
			} else {
				layerLoader.loadAll(fragment);
				fragment.setLoaded();
			}
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void recycleFragment(Fragment fragment) {
		fragment.recycle();
		removeFromLoadingQueue(fragment);
		availableQueue.offer(fragment);
	}

	// TODO: Check performance with and without this. It is not needed, since
	// loadFragment checks for isInitialized(). It helps to keep the
	// loadingQueue small, but it costs time to remove fragments from the queue.
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void removeFromLoadingQueue(Object fragment) {
		while (loadingQueue.remove(fragment)) {
			// noop
		}
	}
}
