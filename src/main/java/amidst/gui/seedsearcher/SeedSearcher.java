package amidst.gui.seedsearcher;

import java.util.function.Consumer;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.MainWindow;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.filter.WorldFilter;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.ProgressReporter;
import amidst.threading.worker.ProgressReportingWorker;

@NotThreadSafe
public class SeedSearcher {
	private final MainWindow mainWindow;
	private final MojangApi mojangApi;
	private final WorkerExecutor workerExecutor;

	private volatile boolean isSearching = false;
	private volatile boolean isStopable = false;
	private volatile boolean isStopRequested = false;

	@CalledOnlyBy(AmidstThread.EDT)
	public SeedSearcher(MainWindow mainWindow, MojangApi mojangApi, WorkerExecutor workerExecutor) {
		this.mainWindow = mainWindow;
		this.mojangApi = mojangApi.createSilentPlayerlessCopy();
		this.workerExecutor = workerExecutor;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void search(SeedSearcherConfiguration configuration, Consumer<WorldSeed> onWorldSeedFound) {
		this.isSearching = true;
		this.isStopable = configuration.isSearchContinuously();
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
	public boolean isStopable() {
		return isStopable;
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void trySearch(ProgressReporter<WorldSeed> reporter, SeedSearcherConfiguration configuration) {
		try {
			doSearch(reporter, configuration.getWorldFilter(), configuration.getWorldType());
		} catch (IllegalStateException | MinecraftInterfaceException e) {
			e.printStackTrace();
			mainWindow.displayException(e);
		} finally {
			this.isSearching = false;
			this.isStopable = false;
			this.isStopRequested = false;
		}
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doSearch(ProgressReporter<WorldSeed> reporter, WorldFilter worldFilter, WorldType worldType)
			throws IllegalStateException, MinecraftInterfaceException {
		do {
			doSearchOne(reporter, worldFilter, worldType);
		} while (isStopable && !isStopRequested);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doSearchOne(ProgressReporter<WorldSeed> reporter, WorldFilter worldFilter, WorldType worldType)
			throws MinecraftInterfaceException {
		while (!isStopRequested) {
			World world = mojangApi.createWorldFromSeed(WorldSeed.random(), worldType);
			if (worldFilter.isValid(world)) {
				reporter.report(world.getWorldSeed());
				break;
			}
		}
	}
}
