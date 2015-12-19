package amidst.threading;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.ThreadSafe;

/**
 * This class executes all invoked runnables in the thread that calls
 * processTasks.
 */
@ThreadSafe
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
