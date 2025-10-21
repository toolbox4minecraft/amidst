package amidst.mojangapi.file;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.threading.WorkerExecutor;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is responsible to provide the version list provided by
 * Mojang's web API.
 */
@NotThreadSafe
public class VersionListProvider {

	/**
	 * The version list that will be populated once the async request
	 * gets back to us.
	 */
	private volatile List<Version> remote;

	/**
	 * Listeners to inform when we've downloaded the version list.
	 */
	private final ConcurrentLinkedQueue<Runnable> listeners = new ConcurrentLinkedQueue<>();

	@CalledOnlyBy(AmidstThread.EDT)
	public VersionListProvider(WorkerExecutor workerExecutor) {
		workerExecutor.run(() -> {
			AmidstLogger.info("Starting to download remote version list.");
			return Version.newRemoteVersionList();
		}, remote -> {
			AmidstLogger.info("Successfully loaded remote version list.");
			this.remote = remote;
			listeners.forEach(Runnable::run);
			listeners.clear();
		}, e -> AmidstLogger.warn(e, "Error while downloading remote version list."));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void onDownloadRemoteFinished(Runnable listener) {
		if (remote == null) {
			listeners.offer(listener);
		}
	}

	/**
	 * Gets the version list provided by Mojang's API, if it's responded.
	 *
	 * @return a list of versions, or {@code null}
	 */
	public List<Version> getRemote() {
		return remote;
	}
}
