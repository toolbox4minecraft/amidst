package amidst.mojangapi.world.export;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.World;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class WorldExporterFactory {
	private final WorkerExecutor workerExecutor;
	private final World world;

	private volatile String progressMessage;

	public WorldExporterFactory(WorkerExecutor workerExecutor, World world) {
		this.workerExecutor = workerExecutor;
		this.world = world;
	}

	public WorldExporter create(WorldExporterConfiguration configuration) {
		return new WorldExporter(workerExecutor, world, configuration, this::setProgressMessage);
	}

	@CalledByAny
	private void setProgressMessage(String progressMessage) {
		this.progressMessage = progressMessage;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public String getProgressMessage() {
		return progressMessage;
	}
}
