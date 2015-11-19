package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueProcessor implements Runnable {
	private ConcurrentLinkedQueue<Fragment> fragmentQueue;
	private ConcurrentLinkedQueue<Fragment> requestQueue;
	private ConcurrentLinkedQueue<Fragment> recycleQueue;

	private Thread currentThread;
	private boolean running = true;

	public QueueProcessor(ConcurrentLinkedQueue<Fragment> fragmentQueue,
			ConcurrentLinkedQueue<Fragment> requestQueue,
			ConcurrentLinkedQueue<Fragment> recycleQueue) {
		this.fragmentQueue = fragmentQueue;
		this.requestQueue = requestQueue;
		this.recycleQueue = recycleQueue;
	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (running) {
			if (requestQueue.isEmpty() && recycleQueue.isEmpty()) {
				Thread.yield();
			} else {
				if (!requestQueue.isEmpty()) {
					processRequestQueueEntry();
				}
				while (!recycleQueue.isEmpty()) {
					processRecycleQueueEntry();
				}
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
