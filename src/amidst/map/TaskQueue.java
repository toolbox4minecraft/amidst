package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskQueue {
	private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();

	public void processTasks() {
		Runnable task;
		while ((task = tasks.poll()) != null) {
			task.run();
		}
	}

	public void invoke(Runnable runnable) {
		tasks.offer(runnable);
	}
}
