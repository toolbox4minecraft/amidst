package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FragmentManager {
	private class QueueProcessor implements Runnable {
		private Thread currentThread;
		private boolean running = true;
		private int sleepTick = 0;

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
					sleep(2);
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
				if (fragment.needsLoading()) {
					fragment.load();
					sleepIfNecessary();
				}
			}
		}

		private void sleepIfNecessary() {
			sleepTick++;
			if (sleepTick == 10) {
				sleep(1);
			}
		}

		private void sleep(long millis) {
			sleepTick = 0;
			try {
				Thread.sleep(millis);
			} catch (InterruptedException ignored) {
			}
		}

		public void startNewThread() {
			running = true;
			currentThread = new Thread(queueProcessor);
			currentThread.start();
		}

		public void gracefullyShutdownCurrentThread() {
			running = false;
			try {
				currentThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public boolean isRunning() {
			return running;
		}
	}

	private ConcurrentLinkedQueue<Fragment> fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> requestQueue = new ConcurrentLinkedQueue<Fragment>();
	private ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<Fragment>();

	private QueueProcessor queueProcessor = new QueueProcessor();
	private Object queueLock = new Object();

	private LayerContainer layerContainer;
	private FragmentCache cache;

	public FragmentManager(LayerContainer layerContainer) {
		this.layerContainer = layerContainer;
		this.cache = new FragmentCache(layerContainer, fragmentQueue);
	}

	public Fragment requestFragment(int x, int y) {
		if (!queueProcessor.isRunning()) {
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

	public void returnFragment(Fragment frag) {
		recycleQueue.offer(frag);
	}

	public void reset() {
		queueProcessor.gracefullyShutdownCurrentThread();
		clearAllQueues();
		cache.resetAllFragments();
	}

	public void setMap(Map map) {
		layerContainer.reloadAllLayers(map);
		queueProcessor.startNewThread();
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
