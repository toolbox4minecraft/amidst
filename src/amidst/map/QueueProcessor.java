package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueProcessor implements Runnable {
	private Thread currentThread;
	private boolean running = true;
	private int sleepTick = 0;
	private ConcurrentLinkedQueue<Fragment> fragmentQueue;
	private ConcurrentLinkedQueue<Fragment> requestQueue;
	private ConcurrentLinkedQueue<Fragment> recycleQueue;

	public QueueProcessor(ConcurrentLinkedQueue<Fragment> fragmentQueue,
			ConcurrentLinkedQueue<Fragment> requestQueue,
			ConcurrentLinkedQueue<Fragment> recycleQueue) {
		this.fragmentQueue = fragmentQueue;
		this.requestQueue = requestQueue;
		this.recycleQueue = recycleQueue;
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
		currentThread = new Thread(this);
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
