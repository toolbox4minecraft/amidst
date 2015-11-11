package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FragmentManager implements Runnable {
	private LayerContainer layerContainer;
	private FragmentCache cache;

	private ConcurrentLinkedQueue<Fragment> fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> requestQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<Fragment>();

	private Object queueLock = new Object();
	private Thread currentThread;
	private boolean running = true;
	private int sleepTick = 0;

	public FragmentManager(LayerContainer layerContainer) {
		this.layerContainer = layerContainer;
		this.cache = new FragmentCache(layerContainer, fragmentQueue);
	}

	public Fragment requestFragment(int x, int y) {
		if (!running) {
			return null;
		}
		Fragment fragment;
		while ((fragment = fragmentQueue.poll()) == null) {
			cache.doubleSize();
		}

		fragment.init(x, y);
		requestQueue.offer(fragment);
		return fragment;
	}

	@Override
	public void run() {
		currentThread.setPriority(Thread.MIN_PRIORITY);

		while (running) {
			if (!requestQueue.isEmpty() || !recycleQueue.isEmpty()) {
				if (!requestQueue.isEmpty()) {
					processRequestQueueEntry();
				}
				while (!recycleQueue.isEmpty()) {
					processRecycleQueueEntry();
				}
			} else {
				sleepTick = 0;
				try {
					Thread.sleep(2);
				} catch (InterruptedException ignored) {
				}
			}
		}

	}

	private void processRecycleQueueEntry() {
		synchronized (queueLock) {
			Fragment fragment = recycleQueue.poll();
			fragment.recycle();
			fragmentQueue.offer(fragment);
		}
	}

	private void processRequestQueueEntry() {
		synchronized (queueLock) {
			Fragment fragment = requestQueue.poll();
			if (fragment.isActive && !fragment.isLoaded) {
				fragment.load();
				sleepIfNecessary();
			}
		}
	}

	public void repaintFragment(Fragment fragment) {
		synchronized (queueLock) {
			fragment.repaint();
		}
	}

	public void repaintFragmentLayer(Fragment fragment, int id) {
		synchronized (queueLock) {
			fragment.repaintImageLayer(id);
		}
	}

	private void sleepIfNecessary() {
		sleepTick++;
		if (sleepTick == 10) {
			sleepTick = 0;
			try {
				Thread.sleep(1);
			} catch (InterruptedException ignored) {
			}
		}
	}

	public void returnFragment(Fragment frag) {
		recycleQueue.offer(frag);
	}

	public void reset() {
		gracefullyShutdownCurrentThread();
		clearAllQueues();
		cache.resetAllFragments();
	}

	public void setMap(Map map) {
		layerContainer.reloadAllLayers(map);
		startNewThread();
	}

	private void startNewThread() {
		running = true;
		currentThread = new Thread(this);
		currentThread.start();
	}

	private void gracefullyShutdownCurrentThread() {
		running = false;
		try {
			currentThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void updateAllLayers(float time) {
		layerContainer.updateAllLayers(time);
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
