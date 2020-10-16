package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

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
	private final ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<>();
	private final FragmentCache cache;
	
	private final Setting<Integer> threadsSetting;
	private ThreadPoolExecutor fragWorkers;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentManager(Iterable<FragmentConstructor> constructors, int numberOfLayers, Setting<Integer> threadsSetting) {
		this.cache = new FragmentCache(availableQueue, loadingQueue, constructors, numberOfLayers);
		this.threadsSetting = threadsSetting;
		this.fragWorkers = createThreadPool();
	}
	
	public ThreadPoolExecutor createThreadPool() {
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadsSetting.get(), new ThreadFactory() {
			private int num;
			
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Fragment-Worker-" + num++);
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.setCorner(coordinates);
		fragment.setState(Fragment.State.INITIALIZED);
		loadingQueue.offer(fragment);
		return fragment;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void recycleFragment(Fragment fragment) {
		recycleQueue.offer(fragment);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentQueueProcessor createQueueProcessor(LayerManager layerManager, Setting<Dimension> dimensionSetting) {
		return new FragmentQueueProcessor(
				availableQueue,
				loadingQueue,
				recycleQueue,
				cache,
				layerManager,
				fragWorkers,
				dimensionSetting);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getAvailableQueueSize() {
		return availableQueue.size();
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
		cache.clear();
		availableQueue.clear();
		loadingQueue.clear();
		recycleQueue.clear();
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	public void restartThreadPool() {
		fragWorkers.shutdownNow();
		this.fragWorkers = createThreadPool();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getCacheSize() {
		return cache.size();
	}
}
