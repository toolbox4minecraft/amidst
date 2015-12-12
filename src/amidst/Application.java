package amidst;

import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
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

@NotThreadSafe
public class Application {
	private final CommandLineParameters parameters;
	private final Settings settings;
	private final GoogleTracker googleTracker;
	private final PlayerInformationCache playerInformationCache;
	private final MojangApi mojangApi;
	private final WorldSurroundingsBuilder worldSurroundingsBuilder;
	private final SeedHistoryLogger seedHistoryLogger;
	private final ThreadMaster threadMaster;
	private final UpdatePrompt updatePrompt;

	private volatile VersionSelectWindow versionSelectWindow;
	private volatile MainWindow mainWindow;

	@CalledOnlyBy(AmidstThread.EDT)
	public Application(CommandLineParameters parameters) {
		this.parameters = parameters;
		this.settings = createSettings();
		this.googleTracker = createGoogleTracker();
		this.playerInformationCache = createPlayerInformationCache();
		this.mojangApi = createMojangApi();
		this.worldSurroundingsBuilder = createWorldSurroundingsBuilder();
		this.seedHistoryLogger = createSeedHistoryLogger();
		this.threadMaster = createThreadMaster();
		this.updatePrompt = createUpdatePrompt();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Settings createSettings() {
		return new Settings(Preferences.userNodeForPackage(Amidst.class));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private GoogleTracker createGoogleTracker() {
		return new GoogleTracker();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private PlayerInformationCache createPlayerInformationCache() {
		return new PlayerInformationCache();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private MojangApi createMojangApi() {
		return new MojangApiBuilder(new WorldBuilder(googleTracker,
				playerInformationCache), parameters.minecraftPath,
				parameters.minecraftLibraries, parameters.minecraftJar,
				parameters.minecraftJson).construct();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private WorldSurroundingsBuilder createWorldSurroundingsBuilder() {
		return new WorldSurroundingsBuilder(settings,
				new LayerBuilder(settings));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private SeedHistoryLogger createSeedHistoryLogger() {
		return new SeedHistoryLogger(parameters.historyPath);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ThreadMaster createThreadMaster() {
		return new ThreadMaster();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private UpdatePrompt createUpdatePrompt() {
		return new UpdatePrompt();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void run() {
		googleTracker.trackApplicationRunning();
		if (mojangApi.canCreateWorld()) {
			displayMainWindow();
		} else {
			displayVersionSelectWindow();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayMainWindow() {
		setMainWindow(new MainWindow(this, settings, mojangApi,
				worldSurroundingsBuilder, seedHistoryLogger, updatePrompt,
				threadMaster));
		setVersionSelectWindow(null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayVersionSelectWindow() {
		setVersionSelectWindow(new VersionSelectWindow(this,
				threadMaster.getWorkerExecutor(), mojangApi, settings));
		setMainWindow(null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setVersionSelectWindow(VersionSelectWindow versionSelectWindow) {
		disposeVersionSelectWindow();
		this.versionSelectWindow = versionSelectWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setMainWindow(MainWindow mainWindow) {
		disposeMainWindow();
		this.mainWindow = mainWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void disposeVersionSelectWindow() {
		VersionSelectWindow versionSelectWindow = this.versionSelectWindow;
		if (versionSelectWindow != null) {
			versionSelectWindow.dispose();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void disposeMainWindow() {
		MainWindow mainWindow = this.mainWindow;
		if (mainWindow != null) {
			mainWindow.dispose();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayLicenseWindow() {
		new LicenseWindow();
	}

	// TODO: use find all occurrences of System.exit();
	@CalledOnlyBy(AmidstThread.EDT)
	public void exitGracefully() {
		dispose();
		System.exit(0);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void exitWithErrorCode(int code) {
		dispose();
		System.exit(code);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		setVersionSelectWindow(null);
		setMainWindow(null);
	}

	@CalledByAny
	void crash(Throwable e, String exceptionText, final String message,
			final String allLogMessages) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				doCrash(message, allLogMessages);
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doCrash(String message, String allLogMessages) {
		new CrashWindow(message, allLogMessages, new Runnable() {
			@Override
			public void run() {
				exitWithErrorCode(4);
			}
		});
	}
}
