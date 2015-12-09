package amidst;

import java.util.prefs.Preferences;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.layer.LayerBuilder;
import amidst.gui.CrashWindow;
import amidst.gui.LicenseWindow;
import amidst.gui.MainWindow;
import amidst.gui.SkinLoader;
import amidst.gui.UpdatePrompt;
import amidst.gui.version.VersionSelectWindow;
import amidst.gui.worldsurroundings.WorldSurroundingsBuilder;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.MojangApiBuilder;
import amidst.threading.ThreadMaster;
import amidst.utilities.SeedHistoryLogger;

public class Application {
	private final CommandLineParameters parameters;
	private final Options options;
	private final MojangApi mojangApi;
	private final WorldSurroundingsBuilder worldSurroundingsBuilder;
	private final SeedHistoryLogger seedHistoryLogger;
	private final ThreadMaster threadMaster;
	private final SkinLoader skinLoader;
	private final UpdatePrompt updatePrompt;

	private volatile VersionSelectWindow versionSelectWindow;
	private volatile MainWindow mainWindow;

	public Application(CommandLineParameters parameters) {
		this.parameters = parameters;
		this.options = createOptions();
		this.mojangApi = createMojangApi();
		this.worldSurroundingsBuilder = createWorldSurroundingsBuilder();
		this.seedHistoryLogger = createSeedHistoryLogger();
		this.threadMaster = createThreadMaster();
		this.skinLoader = createSkinLoader();
		this.updatePrompt = createUpdatePrompt();
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
		return new ThreadMaster(new Runnable() {
			@CalledOnlyBy(AmidstThread.REPAINTER)
			@Override
			public void run() {
				MainWindow window = mainWindow;
				if (window != null) {
					window.tickRepainter();
				}
			}
		}, new Runnable() {
			@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
			@Override
			public void run() {
				MainWindow window = mainWindow;
				if (window != null) {
					window.tickFragmentLoader();
				}
			}
		});
	}

	private SkinLoader createSkinLoader() {
		return new SkinLoader(threadMaster.getWorkerExecutor());
	}

	private UpdatePrompt createUpdatePrompt() {
		return new UpdatePrompt();
	}

	public void run() {
		if (mojangApi.canCreateWorld()) {
			displayMainWindow();
		} else {
			displayVersionSelectWindow();
		}
	}

	public void displayMainWindow() {
		setMainWindow(new MainWindow(this, options, mojangApi,
				worldSurroundingsBuilder, seedHistoryLogger, skinLoader,
				updatePrompt));
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
