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
			Fragment fragment = recycleQueue.poll();
			fragment.reset();
			fragmentQueue.offer(fragment);
		}

		private void processRequestQueueEntry() {
			Fragment fragment = requestQueue.poll();
			if (fragment.needsLoading()) {
				fragment.load();
				sleepIfNecessary();
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

	private FragmentCache cache;

	public FragmentManager(LayerContainer layerContainer) {
		this.cache = new FragmentCache(layerContainer, fragmentQueue);
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
