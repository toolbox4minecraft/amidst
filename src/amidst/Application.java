package amidst;

import java.util.prefs.Preferences;

import amidst.fragment.layer.LayerBuilder;
import amidst.gui.CrashWindow;
import amidst.gui.LicenseWindow;
import amidst.gui.MainWindow;
import amidst.gui.UpdatePrompt;
import amidst.gui.version.VersionSelectWindow;
import amidst.gui.worldsurroundings.WorldSurroundingsBuilder;
import amidst.minecraft.world.World;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.MojangApiBuilder;
import amidst.utilities.SeedHistoryLogger;
import amidst.utilities.SkinLoader;

public class Application {
	private final CommandLineParameters parameters;
	private final Options options;
	private final MojangApi mojangApi;
	private final WorldSurroundingsBuilder worldSurroundingsBuilder;
	private final SeedHistoryLogger seedHistoryLogger;
	private final ThreadMaster threadMaster;
	private final WorkerExecutor workerExecutor;
	private final SkinLoader skinLoader;
	private final UpdatePrompt updateManager;

	private VersionSelectWindow versionSelectWindow;
	private MainWindow mainWindow;

	private World world;

	public Application(CommandLineParameters parameters) {
		this.parameters = parameters;
		this.options = createOptions();
		this.mojangApi = createMojangApi();
		this.worldSurroundingsBuilder = createWorldSurroundingsBuilder();
		this.seedHistoryLogger = createSeedHistoryLogger();
		this.threadMaster = createThreadMaster();
		this.workerExecutor = createWorkerExecutor();
		this.skinLoader = createSkinLoader();
		this.updateManager = createUpdateManager();
	}

	private Options createOptions() {
		return new Options(Preferences.userNodeForPackage(Amidst.class));
	}

	private MojangApi createMojangApi() {
		return new MojangApiBuilder(parameters.minecraftPath,
				parameters.minecraftLibraries, parameters.minecraftJar,
				parameters.minecraftJson).construct();
	}

	private WorldSurroundingsBuilder createWorldSurroundingsBuilder() {
		return new WorldSurroundingsBuilder(options, new LayerBuilder(options));
	}

	private SeedHistoryLogger createSeedHistoryLogger() {
		return new SeedHistoryLogger(parameters.historyPath);
	}

	private ThreadMaster createThreadMaster() {
		return new ThreadMaster(this);
	}

	private WorkerExecutor createWorkerExecutor() {
		return new WorkerExecutor(threadMaster);
	}

	private SkinLoader createSkinLoader() {
		return new SkinLoader(this, workerExecutor);
	}

	private UpdatePrompt createUpdateManager() {
		return new UpdatePrompt();
	}

	public void run() {
		if (mojangApi.hasVersionDirectory()) {
			displayMainWindow();
		} else {
			displayVersionSelectWindow();
		}
	}

	@Deprecated
	public void displayMainWindow() {
		workerExecutor.invokeLater(new Worker<Void>() {
			@Override
			public Void execute() {
				createAndSetLocalMinecraftInterface();
				return null;
			}

			@Override
			public void finished(Void result) {
				doDisplayMainWindow();
			}
		});
	}

	private void createAndSetLocalMinecraftInterface() {
		mojangApi.createAndSetLocalMinecraftInterface();
	}

	private void doDisplayMainWindow() {
		setMainWindow(new MainWindow(this, options, mojangApi));
		setVersionSelectWindow(null);
	}

	public void displayVersionSelectWindow() {
		setVersionSelectWindow(new VersionSelectWindow(this, workerExecutor,
				mojangApi, options));
		setMainWindow(null);
	}

	private void setVersionSelectWindow(VersionSelectWindow versionSelectWindow) {
		if (this.versionSelectWindow != null) {
			this.versionSelectWindow.dispose();
		}
		this.versionSelectWindow = versionSelectWindow;
	}

	private void setMainWindow(MainWindow mainWindow) {
		if (this.mainWindow != null) {
			this.mainWindow.dispose();
		}
		this.mainWindow = mainWindow;
	}

	public void dispose() {
		setVersionSelectWindow(null);
		setMainWindow(null);
	}

	public void displayLicenseWindow() {
		new LicenseWindow();
	}

	public void checkForUpdates() {
		updateManager.check(mainWindow);
	}

	public void checkForUpdatesSilently() {
		updateManager.checkSilently(mainWindow);
	}

	// TODO: use find all occurrences of System.exit();
	public void exitGracefully() {
		dispose();
		System.exit(0);
	}

	public void exitWithErrorCode(int code) {
		dispose();
		System.exit(code);
	}

	public void setWorld(World world) {
		this.world = world;
		if (world != null) {
			seedHistoryLogger.log(world.getSeed());
			mainWindow.clearWorldSurroundings();
			mainWindow.setWorldSurroundings(worldSurroundingsBuilder
					.create(world));
			skinLoader.loadSkins(world.getMovablePlayers());
		}
	}

	public World getWorld() {
		return world;
	}

	void crash(Throwable e, String exceptionText, String message,
			String allLogMessages) {
		new CrashWindow(message, allLogMessages, new Runnable() {
			@Override
			public void run() {
				exitWithErrorCode(4);
			}
		});
	}

	public void tickRepainter() {
		if (mainWindow != null) {
			mainWindow.tickRepainter();
		}
	}

	public void tickFragmentLoader() {
		if (mainWindow != null) {
			mainWindow.tickFragmentLoader();
		}
	}

	public void finishedLoadingPlayerSkin() {
		if (world != null) {
			world.reloadPlayerWorldIcons();
		}
		if (mainWindow != null) {
			mainWindow.reloadPlayerLayer();
		}
	}

	@Deprecated
	public MojangApi getMojangApi() {
		return mojangApi;
	}
}
