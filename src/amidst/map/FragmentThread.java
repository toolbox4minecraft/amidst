package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.Log;

public class FragmentThread extends Thread {
	private ConcurrentLinkedQueue<Fragment> requestQueue = new ConcurrentLinkedQueue<Fragment>();
	private boolean running = true;
	public int threadId;
	
	public FragmentThread(int threadId) {
		this.threadId = threadId;
		setName("FragmentThread" + threadId);
	}
	
	public void offer(Fragment fragment) {
		requestQueue.offer(fragment);
	}
	
	public void run() {
		Fragment frag;
		while (running) {
			if((frag = requestQueue.poll()) != null) {
				synchronized (frag) {
					if (frag.isActive && !frag.isLoaded) {
						frag.load();
						try {
							Thread.sleep(1L);
						} catch (InterruptedException ignored) {}
					}
				}
			} else {
				try {
					Thread.sleep(2L);
				} catch (InterruptedException ignored) { }
			}
		}
		
	}
	
	public void shutdown() {
		running = false;
	}
}
