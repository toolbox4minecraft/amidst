package amidst.mojangapi.file;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.parsing.FormatException;
import amidst.threading.WorkerExecutor;

/**
 * This class is responsible to provide the version list. It will instantly
 * provide the local version list, but try to download the remote version list
 * in the background. Listeners can be registered to be informed about a
 * successful download.
 */
@NotThreadSafe
public class VersionListProvider {
	@CalledOnlyBy(AmidstThread.EDT)
	public static VersionListProvider createLocalAndStartDownloadingRemote(WorkerExecutor workerExecutor)
			throws FormatException,
			IOException {
		VersionListProvider versionListProvider = new VersionListProvider(VersionList.newLocalVersionList());
		versionListProvider.startDownloading(workerExecutor);
		return versionListProvider;
	}

	private final ConcurrentLinkedQueue<Runnable> listeners = new ConcurrentLinkedQueue<>();
	private final VersionList local;
	private volatile VersionList remote;

	@CalledOnlyBy(AmidstThread.EDT)
	public VersionListProvider(VersionList local) {
		this.local = local;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void onDownloadRemoteFinished(Runnable listener) {
		if (remote == null) {
			listeners.offer(listener);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void startDownloading(WorkerExecutor workerExecutor) {
		workerExecutor.run(this::doDownload, this::finishedDownload, this::downloadFailed);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private VersionList doDownload() throws FormatException, IOException {
		AmidstLogger.info("Starting to download remote version list.");
		return VersionList.newRemoteVersionList();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void finishedDownload(VersionList remote) {
		AmidstLogger.info("Successfully loaded remote version list.");
		this.remote = remote;
		listeners.forEach(Runnable::run);
		listeners.clear();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void downloadFailed(Exception e) {
		AmidstLogger.warn(e, "Error while downloading remote version list.");
	}

	@CalledByAny
	public VersionList getRemoteOrElseLocal() {
		VersionList remote = this.remote;
		if (remote != null) {
			return remote;
		} else {
			return local;
		}
	}
}
