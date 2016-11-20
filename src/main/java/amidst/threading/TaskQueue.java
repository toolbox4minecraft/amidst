package amidst.threading;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.ThreadSafe;

/**
 * This class executes all invoked runnables in the thread that calls
 * processTasks.
 */
@ThreadSafe
public class TaskQueue {
	private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

	/**
	 * Executes all tasks. Returns true, if at least one task was executed.
	 */
	public boolean processTasks() {
		boolean result = false;
		Runnable task;
		while ((task = tasks.poll()) != null) {
			result = true;
			task.run();
		}
		return result;
	}

	public void invoke(Runnable runnable) {
		tasks.offer(runnable);
	}
}
