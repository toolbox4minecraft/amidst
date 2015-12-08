package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.fragment.layer.LayerLoader;
import amidst.threading.TaskQueue;

/**
 * This class is thread-safe, as long as only one thread calls the method
 * tick(). It executes everything in the thread that calls the method tick().
 */
public class FragmentQueueProcessor {
	private final TaskQueue taskQueue = new TaskQueue();

	private final ConcurrentLinkedQueue<Fragment> availableQueue;
	private final ConcurrentLinkedQueue<Fragment> loadingQueue;
	private final ConcurrentLinkedQueue<Fragment> resetQueue;
	private final FragmentCache cache;
	private final LayerLoader layerManager;

	public FragmentQueueProcessor(
			ConcurrentLinkedQueue<Fragment> availableQueue,
			ConcurrentLinkedQueue<Fragment> loadingQueue,
			ConcurrentLinkedQueue<Fragment> resetQueue, FragmentCache cache,
			LayerLoader layerManager) {
		this.availableQueue = availableQueue;
		this.loadingQueue = loadingQueue;
		this.resetQueue = resetQueue;
		this.cache = cache;
		this.layerManager = layerManager;
	}

	/**
	 * This method might be called from any thread.
	 */
	public void invalidateLayer(final int layerId) {
		taskQueue.invoke(new Runnable() {
			@Override
			public void run() {
				doInvalidateLayer(layerId);
			}
		});
	}

	/**
	 * This method is only called from the method tick(). This ensures, that the
	 * layerManager is only used by a single thread.
	 */
	private void doInvalidateLayer(int layerId) {
		layerManager.invalidateLayer(layerId);
		cache.reloadAll();
	}

	/**
	 * This method is only called from the fragment loading thread.
	 */
	public void tick() {
		taskQueue.processTasks();
		processResetQueue();
		Fragment fragment;
		while ((fragment = loadingQueue.poll()) != null) {
			loadFragment(fragment);
			taskQueue.processTasks();
			processResetQueue();
		}
		layerManager.clearInvalidatedLayers();
	}

	private void processResetQueue() {
		Fragment fragment;
		while ((fragment = resetQueue.poll()) != null) {
			resetFragment(fragment);
		}
	}

	private void loadFragment(Fragment fragment) {
		if (fragment.isInitialized()) {
			if (fragment.isLoaded()) {
				layerManager.reloadInvalidated(fragment);
			} else {
				layerManager.loadAll(fragment);
				fragment.setLoaded(true);
			}
		}
	}

	private void resetFragment(Fragment fragment) {
		fragment.setLoaded(false);
		fragment.setInitialized(false);
		removeFromLoadingQueue(fragment);
		availableQueue.offer(fragment);
	}

	// TODO: Check performance with and without this. It is not needed, since
	// loadFragment checks for isInitialized(). It helps to keep the
	// loadingQueue small, but it costs time to remove fragments from the queue.
	private void removeFromLoadingQueue(Object fragment) {
		while (loadingQueue.remove(fragment)) {
			// noop
		}
	}
}
