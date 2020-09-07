package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.layer.LayerManager;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.settings.Setting;

@NotThreadSafe
public class FragmentManager {
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedDeque<Fragment> recycleQueue = new ConcurrentLinkedDeque<>();
	
	private final AvailableFragmentCache availableCache;
	private final OffScreenFragmentCache offscreenCache;
	
	private final Setting<Integer> threadsSetting;
	private final ScheduledExecutorService cleanerThread;
	private ThreadPoolExecutor fragWorkers;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentManager(Iterable<FragmentConstructor> constructors, int numberOfLayers, Setting<Integer> threadsSetting) {
		this.availableCache = new AvailableFragmentCache(constructors, numberOfLayers);
		this.offscreenCache = new OffScreenFragmentCache(recycleQueue);
		this.threadsSetting = threadsSetting;
		this.cleanerThread = createCleanerThread();
		this.fragWorkers = createThreadPool();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public ThreadPoolExecutor createThreadPool() {
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadsSetting.get(), new ThreadFactory() {
			private int num;
			
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Fragment-Worker-" + num++);
			}
		});
	}

	private ScheduledExecutorService createCleanerThread() {
		ScheduledExecutorService scheduledExecutor =
				Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "CacheCleanerThread"));
		scheduledExecutor.scheduleAtFixedRate(() -> {
			offscreenCache.clean();
			availableCache.clean();
		}, 0, 500, TimeUnit.MILLISECONDS); // cleans both caches 2 times a second
		return scheduledExecutor;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void restart() {
		fragWorkers.shutdownNow();
		this.fragWorkers = createThreadPool();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void shutdownCleaner() {
		cleanerThread.shutdownNow();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void invalidateCaches() {
		offscreenCache.invalidate();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		// We get and remove the fragments that come on screen from the loading cache,
		// returning it if it exists.
		Fragment fragment = offscreenCache.remove(coordinates);
		if (fragment != null) {
			return fragment;
		}

		fragment = availableCache.getOrCreate();
		fragment.setCorner(coordinates);
		fragment.setInitialized();
		loadingQueue.offer(fragment);
		return fragment;
	}

	/**
	 * Called when a fragment is no longer shown on the screen.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public void retireFragment(Fragment fragment) {
		if (!fragment.isLoaded() && fragment.recycle()) { // try to recycle if it's not loaded
			// Send it back to the available cache
			loadingQueue.remove(fragment);
			availableCache.put(fragment);
		} else {
			// We store the fragment if it's loaded or not properly recycling and it goes offscreen
			offscreenCache.put(fragment);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentQueueProcessor createQueueProcessor(
			LayerManager layerManager,
			Setting<Dimension> dimensionSetting,
			FragmentGraph graph) {
		
		return new FragmentQueueProcessor(
				loadingQueue,
				recycleQueue,
				availableCache,
				offscreenCache,
				layerManager,
				dimensionSetting,
				graph,
				fragWorkers);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getAvailableCacheSize() {
		return availableCache.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getOffscreenCacheSize() {
		return offscreenCache.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	public void clear() {
		offscreenCache.clear();
		loadingQueue.clear();
		recycleQueue.clear();
	}
}
