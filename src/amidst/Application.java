package amidst;

import java.util.prefs.Preferences;

import amidst.fragment.layer.LayerBuilder;
import amidst.gui.CrashWindow;
import amidst.gui.LicenseWindow;
import amidst.gui.MainWindow;
import amidst.gui.UpdatePrompt;
import amidst.gui.version.VersionSelectWindow;
import amidst.gui.worldsurroundings.WorldSurroundingsBuilder;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.MojangApiBuilder;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.player.PlayerInformationCache;
import amidst.threading.ThreadMaster;
import amidst.utilities.GoogleTracker;
import amidst.utilities.SeedHistoryLogger;

public class Application {
	private final CommandLineParameters parameters;
	private final Options options;
	private final GoogleTracker googleTracker;
	private final PlayerInformationCache playerInformationCache;
	private final MojangApi mojangApi;
	private final WorldSurroundingsBuilder worldSurroundingsBuilder;
	private final SeedHistoryLogger seedHistoryLogger;
	private final ThreadMaster threadMaster;
	private final UpdatePrompt updatePrompt;

	private volatile VersionSelectWindow versionSelectWindow;
	private volatile MainWindow mainWindow;

	public Application(CommandLineParameters parameters) {
		this.parameters = parameters;
		this.options = createOptions();
		this.googleTracker = createGoogleTracker();
		this.playerInformationCache = createPlayerInformationCache();
		this.mojangApi = createMojangApi();
		this.worldSurroundingsBuilder = createWorldSurroundingsBuilder();
		this.seedHistoryLogger = createSeedHistoryLogger();
		this.threadMaster = createThreadMaster();
		this.updatePrompt = createUpdatePrompt();
	}

	private Options createOptions() {
		return new Options(Preferences.userNodeForPackage(Amidst.class));
	}

	private GoogleTracker createGoogleTracker() {
		return new GoogleTracker();
	}

	private PlayerInformationCache createPlayerInformationCache() {
		return new PlayerInformationCache();
	}

	private MojangApi createMojangApi() {
		return new MojangApiBuilder(new WorldBuilder(googleTracker,
				playerInformationCache), parameters.minecraftPath,
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
		return new ThreadMaster();
	}

	private UpdatePrompt createUpdatePrompt() {
		return new UpdatePrompt();
	}

	public void run() {
		googleTracker.trackApplicationRunning();
		if (mojangApi.canCreateWorld()) {
			displayMainWindow();
		} else {
			displayVersionSelectWindow();
		}
	}

	public void displayMainWindow() {
		setMainWindow(new MainWindow(this, options, mojangApi,
				worldSurroundingsBuilder, seedHistoryLogger, updatePrompt,
				threadMaster));
		setVersionSelectWindow(null);
	}

	public void displayVersionSelectWindow() {
		setVersionSelectWindow(new VersionSelectWindow(this,
				threadMaster.getWorkerExecutor(), mojangApi, options));
		setMainWindow(null);
	}

	private void setVersionSelectWindow(VersionSelectWindow versionSelectWindow) {
		disposeVersionSelectWindow();
		this.versionSelectWindow = versionSelectWindow;
	}

	private void setMainWindow(MainWindow mainWindow) {
		disposeMainWindow();
		this.mainWindow = mainWindow;
	}

	private void disposeVersionSelectWindow() {
		VersionSelectWindow versionSelectWindow = this.versionSelectWindow;
		if (versionSelectWindow != null) {
			versionSelectWindow.dispose();
		}
	}

	private void disposeMainWindow() {
		MainWindow mainWindow = this.mainWindow;
		if (mainWindow != null) {
			mainWindow.dispose();
		}
	}

	public void displayLicenseWindow() {
		new LicenseWindow();
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

	public void dispose() {
		setVersionSelectWindow(null);
		setMainWindow(null);
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
}
