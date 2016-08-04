package amidst.mojangapi.world.export;

import java.util.function.Consumer;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.World;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.ProgressReporter;

@NotThreadSafe
public class WorldExporter {
	private final WorkerExecutor workerExecutor;
	@SuppressWarnings("unused")
	private final World world;
	@SuppressWarnings("unused")
	private final WorldExporterConfiguration configuration;
	private final Consumer<String> progressListener;

	public WorldExporter(
			WorkerExecutor workerExecutor,
			World world,
			WorldExporterConfiguration configuration,
			Consumer<String> progressListener) {
		this.workerExecutor = workerExecutor;
		this.world = world;
		this.configuration = configuration;
		this.progressListener = progressListener;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void export() {
		workerExecutor.<String> run(this::doExport, progressListener, this::onFinished);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doExport(ProgressReporter<String> progressReporter) {
		// TODO: implement me!
		progressReporter.report("Exporting the world ...");
		throw new UnsupportedOperationException("implement me!");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void onFinished() {
		// TODO: implement me!
		progressListener.accept(null);
	}
}
