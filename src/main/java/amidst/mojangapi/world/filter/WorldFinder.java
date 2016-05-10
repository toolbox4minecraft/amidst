package amidst.mojangapi.world.filter;

import java.io.File;
import java.io.IOException;

import amidst.gui.main.MainWindow;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.filter.WorldFilterJson;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.Worker;

public class WorldFinder {
	private final MojangApi originalMojangApi;
	private final MojangApi mojangApi;

	private WorldFilter worldFilter;
	private boolean continuous = false;
	private boolean searching = false;

	public WorldFinder(MojangApi originalMojangApi) throws LocalMinecraftInterfaceCreationException {
		this.originalMojangApi = originalMojangApi;
		this.mojangApi = originalMojangApi.createSilentPlayerlessCopy();
	}

	public void configureFromFile(File file) throws MojangApiParsingException, IOException {
		if (file.exists()) {
			WorldFilterJson config = JsonReader.readWorldFilters(file);
			config.configureWorldFinder(this);
		}
	}

	public void setWorldFilter(WorldFilter filter) {
		this.worldFilter = filter;
	}

	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	public boolean isSearching() {
		return searching;
	}

	public boolean canFindWorlds() {
		return worldFilter != null && worldFilter.hasFilters();
	}

	public void findRandomWorld(WorldType worldType, WorkerExecutor workerExecutor, MainWindow mainWindow) {
		searching = true;
		workerExecutor.run(new Worker() {
			@Override
			public void run() {
				try {
					do {
						WorldSeed worldSeed = WorldFinder.this.findRandomWorld(worldType);
						mainWindow.setWorld(originalMojangApi.createWorldFromSeed(worldSeed, worldType));
					} while (continuous);
				} catch (MinecraftInterfaceException e) {
					e.printStackTrace();
					mainWindow.displayException(e);
				} finally {
					searching = false;
				}
			}
		});
	}

	private WorldSeed findRandomWorld(WorldType worldType) throws IllegalStateException, MinecraftInterfaceException {
		World world;
		do {
			WorldSeed worldSeed = WorldSeed.random();
			world = mojangApi.createWorldFromSeed(worldSeed, worldType);
		} while (!worldFilter.isValid(world));
		return world.getWorldSeed();
	}
}
