package amidst.gui.seedsearcher;

import java.util.function.Consumer;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.MainWindowDialogs;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.ProgressReporter;
import amidst.threading.worker.ProgressReportingWorker;

@NotThreadSafe
public class SeedSearcher {
	private final MainWindowDialogs dialogs;
	private final RunningLauncherProfile runningLauncherProfile;
	private final WorkerExecutor workerExecutor;

	private volatile boolean isSearching = false;
	private volatile boolean isStopRequested = false;

	@CalledOnlyBy(AmidstThread.EDT)
	public SeedSearcher(
			MainWindowDialogs dialogs,
			RunningLauncherProfile runningLauncherProfile,
			WorkerExecutor workerExecutor) {
		this.dialogs = dialogs;
		this.runningLauncherProfile = runningLauncherProfile;
		this.workerExecutor = workerExecutor;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void search(SeedSearcherConfiguration configuration, Consumer<WorldSeed> onWorldSeedFound) {
		this.isSearching = true;
		this.isStopRequested = false;
		workerExecutor.run(createSearcher(configuration), onWorldSeedFound);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ProgressReportingWorker<WorldSeed> createSearcher(SeedSearcherConfiguration configuration) {
		return reporter -> this.trySearch(reporter, configuration);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void stop() {
		this.isStopRequested = true;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		stop();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isSearching() {
		return isSearching;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isStopRequested() {
		return isStopRequested;
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void trySearch(ProgressReporter<WorldSeed> reporter, SeedSearcherConfiguration configuration) {
		try {
			doSearch(reporter, configuration);
		} catch (IllegalStateException | MinecraftInterfaceException e) {
			AmidstLogger.warn(e);
			dialogs.displayError(e);
		} finally {
			this.isSearching = false;
			this.isStopRequested = false;
		}
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doSearch(ProgressReporter<WorldSeed> reporter, SeedSearcherConfiguration configuration)
			throws IllegalStateException,
			MinecraftInterfaceException {
		do {
			doSearchOne(reporter, configuration);
		} while (configuration.isSearchContinuously() && !isStopRequested);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doSearchOne(ProgressReporter<WorldSeed> reporter, SeedSearcherConfiguration configuration)
			throws IllegalStateException,
			MinecraftInterfaceException {
		while (!isStopRequested) {
			World world = runningLauncherProfile.createWorldFromSeed(WorldSeed.random(), configuration.getWorldType());
			if (configuration.getWorldFilter().isValid(world)) {
				reporter.report(world.getWorldSeed());
				world.dispose();
				break;
			}
			world.dispose();
		}
	}
}
