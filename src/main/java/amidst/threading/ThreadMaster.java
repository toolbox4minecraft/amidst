package amidst.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;

@ThreadSafe
public class ThreadMaster {
	private static final Runnable NOOP = new Runnable() {
		@Override
		public void run() {
			// noop
		}
	};

	private final ScheduledExecutorService repaintExecutorService;
	private final ScheduledExecutorService fragmentLoaderExecutorService;
	private final ExecutorService workerExecutorService;
	private final WorkerExecutor workerExecutor;

	private volatile Runnable onRepaintTick;
	private volatile Runnable onFragmentLoadTick;
	
	private boolean needsRepaint;
	private boolean needsProcessFragments;
	private static ThreadMaster theThreadMaster;

	public ThreadMaster() {
		this.repaintExecutorService = createRepaintExecutorService();
		this.fragmentLoaderExecutorService = createFragmentLoaderExecutorService();
		this.workerExecutorService = createWorkerExecutorService();
		this.workerExecutor = createWorkerExecutor();
		this.onRepaintTick = NOOP;
		this.onFragmentLoadTick = NOOP;
		theThreadMaster = this;
	}

	public static ThreadMaster theThreadMaster() { return theThreadMaster; }
	
	private ScheduledExecutorService createRepaintExecutorService() {
		return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	private ScheduledExecutorService createFragmentLoaderExecutorService() {
		return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				thread.setPriority(Thread.MIN_PRIORITY);
				return thread;
			}
		});
	}

	private ExecutorService createWorkerExecutorService() {
		return Executors.newCachedThreadPool(new ThreadFactory() {
			int workerNum;
			
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.setName("WorkerExecutor-" + workerNum++);
				return thread;
			}
		});
	}

	private WorkerExecutor createWorkerExecutor() {
		return new WorkerExecutor(workerExecutorService);
	}

	// schedule a repaint if we have not done so already
	public void setNeedsRepaint() {
		if (needsRepaint)
			return;
		needsRepaint = true;
		repaintExecutorService.schedule(new Runnable() {
			@CalledOnlyBy(AmidstThread.REPAINTER)
			@Override
			public void run() {
				onRepaintTick.run();
				needsRepaint = false;
			}
		}, 20, TimeUnit.MILLISECONDS);
		
		needToProcessFragments();
	}
	
	// schedule fragment processing if it's not already planned
	public void needToProcessFragments() {
		if (needsProcessFragments)
			return;
		needsProcessFragments = true;
		fragmentLoaderExecutorService.schedule(new Runnable() {
			@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
			@Override
			public void run() {
				needsProcessFragments = false;
				onFragmentLoadTick.run();
			}
		}, 20, TimeUnit.MILLISECONDS);
	}

	public WorkerExecutor getWorkerExecutor() {
		return workerExecutor;
	}

	public void setOnRepaintTick(Runnable onRepaintTick) {
		this.onRepaintTick = onRepaintTick;
	}

	public void setOnFragmentLoadTick(Runnable onFragmentLoadTick) {
		this.onFragmentLoadTick = onFragmentLoadTick;
	}

	public void clearOnRepaintTick() {
		this.onRepaintTick = NOOP;
	}

	public void clearOnFragmentLoadTick() {
		this.onFragmentLoadTick = NOOP;
	}
}
