package amidst.mojangapi.world.export;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.World;

@NotThreadSafe
public class WorldExporter {
	private final World world;
	private final ExportConfiguration configuration;

	public WorldExporter(World world, ExportConfiguration configuration) {
		this.world = world;
		this.configuration = configuration;
	}

	public void export() {
		throw new UnsupportedOperationException("execute export functionality");
	}
}
