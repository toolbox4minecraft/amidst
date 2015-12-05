package amidst;

import java.io.File;
import java.net.MalformedURLException;
import java.util.prefs.Preferences;

import amidst.fragment.layer.LayerBuilder;
import amidst.gui.CrashWindow;
import amidst.gui.LicenseWindow;
import amidst.gui.MainWindow;
import amidst.gui.UpdatePrompt;
import amidst.gui.version.VersionSelectWindow;
import amidst.gui.worldsurroundings.WorldSurroundingsBuilder;
import amidst.logging.Log;
import amidst.minecraft.IMinecraftInterface;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.minecraft.Minecraft;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.World;
import amidst.utilities.SeedHistoryLogger;
import amidst.utilities.SkinLoader;
import amidst.version.MinecraftProfile;

public class Application {
	private final CommandLineParameters parameters;
	private final Options options;
	private final WorldSurroundingsBuilder worldSurroundingsBuilder;
	private final SeedHistoryLogger seedHistoryLogger;
	private final ThreadMaster threadMaster;
	private final LongRunningIOExecutor longRunningIOExecutor;
	private final SkinLoader skinLoader;
	private final UpdatePrompt updateManager;

	private VersionSelectWindow versionSelectWindow;
	private MainWindow mainWindow;

	private World world;

	public Application(CommandLineParameters parameters) {
		this.parameters = parameters;
		initLocalMinecraftInstallation();
		this.options = createOptions();
		this.worldSurroundingsBuilder = createWorldSurroundingsBuilder();
		this.seedHistoryLogger = createSeedHistoryLogger();
		this.threadMaster = createThreadMaster();
		this.longRunningIOExecutor = createLongRunningIOExecutor();
		this.skinLoader = createSkinLoader();
		this.updateManager = createUpdateManager();
	}

	private void initLocalMinecraftInstallation() {
		LocalMinecraftInstallation
				.initMinecraftDirectory(parameters.minecraftPath);
		LocalMinecraftInstallation
				.initMinecraftLibraries(parameters.minecraftLibraries);
	}

	private Options createOptions() {
		return new Options(Preferences.userNodeForPackage(Amidst.class),
				new File(LocalMinecraftInstallation.getMinecraftDirectory(),
						"bin/minecraft.jar"));
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

	private LongRunningIOExecutor createLongRunningIOExecutor() {
		return new LongRunningIOExecutor(threadMaster);
	}

	private SkinLoader createSkinLoader() {
		return new SkinLoader(this, longRunningIOExecutor);
	}

	private UpdatePrompt createUpdateManager() {
		return new UpdatePrompt();
	}

	public void displayVersionSelectWindow() {
		setVersionSelectWindow(new VersionSelectWindow(this,
				longRunningIOExecutor, options.lastProfile));
		setMainWindow(null);
	}

	public void displayMainWindow(MinecraftProfile profile) {
		LocalMinecraftInstallation.initProfileDirectory(profile.getGameDir());
		doDisplayMainWindow(createLocalMinecraftInterface(profile.getJarFile()));
	}

	public void displayMainWindow(String jarFile, String gameDirectory) {
		LocalMinecraftInstallation.initProfileDirectory(gameDirectory);
		doDisplayMainWindow(createLocalMinecraftInterface(new File(jarFile)));
	}

	private void doDisplayMainWindow(IMinecraftInterface minecraftInterface) {
		MinecraftUtil.setInterface(minecraftInterface);
		setMainWindow(new MainWindow(this, options));
		setVersionSelectWindow(null);
	}

	private IMinecraftInterface createLocalMinecraftInterface(File jarFile) {
		try {
			return new Minecraft(jarFile, parameters.minecraftJson)
					.createInterface();
		} catch (MalformedURLException e) {
			Log.crash(e, "MalformedURLException on Minecraft load.");
			return null;
		}
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
}
