package amidst.mojangapi.world.filter;

import java.io.File;
import java.io.IOException;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
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

public class WorldFinder {
	private final MojangApi originalMojangApi;
	private final MojangApi mojangApi;
	private final MainWindow mainWindow;

	private WorldFilter worldFilter;
	private boolean continuous = false;
	private boolean searching = false;

	public WorldFinder(MojangApi originalMojangApi, MainWindow mainWindow)
			throws LocalMinecraftInterfaceCreationException {
		this.originalMojangApi = originalMojangApi;
		this.mainWindow = mainWindow;
		this.mojangApi = originalMojangApi.createSilentPlayerlessCopy();
	}

	public void configureFromFile(File file) throws MojangApiParsingException, IOException {
		if (file.exists()) {
			WorldFilterJson worldFilterJson = JsonReader.readWorldFilter(file);
			this.continuous = worldFilterJson.isContinuousSearch();
			this.worldFilter = worldFilterJson.createWorldFilter();
		}
	}

	public boolean isSearching() {
		return searching;
	}

	public boolean canFindWorlds() {
		return worldFilter != null && worldFilter.hasFilters();
	}

	public void findRandomWorld(WorldType worldType, WorkerExecutor workerExecutor) {
		searching = true;
		workerExecutor.run(() -> doFind(worldType));
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doFind(WorldType worldType) {
		try {
			do {
				WorldSeed worldSeed = findRandomWorld(worldType);
				mainWindow.setWorld(originalMojangApi.createWorldFromSeed(worldSeed, worldType));
			} while (continuous);
		} catch (MinecraftInterfaceException e) {
			e.printStackTrace();
			mainWindow.displayException(e);
		} finally {
			searching = false;
		}
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private WorldSeed findRandomWorld(WorldType worldType) throws IllegalStateException, MinecraftInterfaceException {
		World world;
		do {
			world = mojangApi.createWorldFromSeed(WorldSeed.random(), worldType);
		} while (!worldFilter.isValid(world));
		return world.getWorldSeed();
	}
}
