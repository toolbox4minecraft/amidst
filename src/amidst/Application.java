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
import amidst.minecraft.remote.RemoteMinecraft;
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
	private final SkinLoader skinLoader;
	private final UpdatePrompt updateManager;

	private VersionSelectWindow versionSelectWindow;
	private MainWindow mapWindow;

	private World world;

	public Application(CommandLineParameters parameters) {
		this.parameters = parameters;
		initLocalMinecraftInstallation();
		this.options = createOptions();
		this.worldSurroundingsBuilder = createWorldSurroundingsBuilder();
		this.seedHistoryLogger = createSeedHistoryLogger();
		this.threadMaster = createThreadMaster();
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

	private SkinLoader createSkinLoader() {
		return new SkinLoader(this, threadMaster);
	}

	private UpdatePrompt createUpdateManager() {
		return new UpdatePrompt();
	}

	public void displayVersionSelectWindow() {
		setVersionSelectWindow(new VersionSelectWindow(this,
				options.lastProfile));
		setMapWindow(null);
	}

	public void displayMapWindow(RemoteMinecraft minecraftInterface) {
		doDisplayMapWindow(minecraftInterface);
	}

	public void displayMapWindow(MinecraftProfile profile) {
		LocalMinecraftInstallation.initProfileDirectory(profile.getGameDir());
		doDisplayMapWindow(createLocalMinecraftInterface(profile.getJarFile()));
	}

	public void displayMapWindow(String jarFile, String gameDirectory) {
		LocalMinecraftInstallation.initProfileDirectory(gameDirectory);
		doDisplayMapWindow(createLocalMinecraftInterface(new File(jarFile)));
	}

	private void doDisplayMapWindow(IMinecraftInterface minecraftInterface) {
		MinecraftUtil.setInterface(minecraftInterface);
		setMapWindow(new MainWindow(this, options));
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

	private void setMapWindow(MainWindow mapWindow) {
		if (this.mapWindow != null) {
			this.mapWindow.dispose();
		}
		this.mapWindow = mapWindow;
	}

	public void dispose() {
		setVersionSelectWindow(null);
		setMapWindow(null);
	}

	public void displayLicenseWindow() {
		new LicenseWindow();
	}

	public void checkForUpdates() {
		updateManager.check(mapWindow);
	}

	public void checkForUpdatesSilently() {
		updateManager.checkSilently(mapWindow);
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
			mapWindow.clearWorldSurroundings();
			mapWindow.setWorldSurroundings(worldSurroundingsBuilder
					.create(world));
			skinLoader.loadSkins(world.getMovablePlayers());
		}
	}

	public MainWindow getMapWindow() {
		return mapWindow;
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
		if (mapWindow != null) {
			mapWindow.tickRepainter();
		}
	}

	public void tickFragmentLoader() {
		if (mapWindow != null) {
			mapWindow.tickFragmentLoader();
		}
	}

	public void finishedLoadingPlayerSkin() {
		if (world != null) {
			world.reloadPlayerWorldIcons();
		}
		if (mapWindow != null) {
			mapWindow.reloadPlayerLayer();
		}
	}
}
