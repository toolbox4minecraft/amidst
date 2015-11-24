package amidst;

import java.io.File;
import java.net.MalformedURLException;

import amidst.gui.CrashWindow;
import amidst.gui.LicenseWindow;
import amidst.gui.MapWindow;
import amidst.gui.UpdatePrompt;
import amidst.gui.version.VersionSelectWindow;
import amidst.logging.Log;
import amidst.map.FragmentCache;
import amidst.map.FragmentFactory;
import amidst.map.LayerContainerFactory;
import amidst.map.SkinLoader;
import amidst.minecraft.IMinecraftInterface;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.minecraft.Minecraft;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.remote.RemoteMinecraft;
import amidst.minecraft.world.World;
import amidst.preferences.BiomeColorProfile;
import amidst.utilities.SeedHistoryLogger;
import amidst.version.MinecraftProfile;

public class Application {
	private ThreadMaster threadMaster = new ThreadMaster(this);
	private SkinLoader skinLoader = new SkinLoader(this, threadMaster);
	private SeedHistoryLogger seedHistoryLogger;
	private UpdatePrompt updateManager = new UpdatePrompt();
	private FragmentCache fragmentCache = new FragmentCache(
			new FragmentFactory(), new LayerContainerFactory());

	private VersionSelectWindow versionSelectWindow;
	private MapWindow mapWindow;

	private World world;

	private CommandLineOptions options;

	public Application(CommandLineOptions options) {
		this.options = options;
		initSeedHistoryLogger();
		initLocalMinecraftInstallation();
		scanForBiomeColorProfiles();
	}

	private void initSeedHistoryLogger() {
		this.seedHistoryLogger = new SeedHistoryLogger(options.historyPath);
	}

	private void initLocalMinecraftInstallation() {
		LocalMinecraftInstallation
				.initMinecraftDirectory(options.minecraftPath);
		LocalMinecraftInstallation
				.initMinecraftLibraries(options.minecraftLibraries);
	}

	private void scanForBiomeColorProfiles() {
		BiomeColorProfile.scan();
	}

	public void displayVersionSelectWindow() {
		setVersionSelectWindow(new VersionSelectWindow(this));
		setMapWindow(null);
	}

	public void displayMapWindow(RemoteMinecraft minecraftInterface) {
		displayMapWindow(minecraftInterface);
	}

	public void displayMapWindow(MinecraftProfile profile) {
		LocalMinecraftInstallation.initProfileDirectory(profile.getGameDir());
		displayMapWindow(createLocalMinecraftInterface(profile.getJarFile()));
	}

	public void displayMapWindow(String jarFile, String gameDirectory) {
		LocalMinecraftInstallation.initProfileDirectory(gameDirectory);
		displayMapWindow(createLocalMinecraftInterface(new File(jarFile)));
	}

	private void displayMapWindow(IMinecraftInterface minecraftInterface) {
		MinecraftUtil.setInterface(minecraftInterface);
		setMapWindow(new MapWindow(this));
		setVersionSelectWindow(null);
	}

	private IMinecraftInterface createLocalMinecraftInterface(File jarFile) {
		try {
			return new Minecraft(jarFile, options.minecraftJson)
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

	private void setMapWindow(MapWindow mapWindow) {
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
			mapWindow.clearWorld();
			mapWindow.initWorld();
			if (world.isFileWorld()) {
				skinLoader
						.loadSkins(world.getAsFileWorld().getMovablePlayers());
			}
		}
	}

	public MapWindow getMapWindow() {
		return mapWindow;
	}

	public World getWorld() {
		return world;
	}

	public FragmentCache getFragmentCache() {
		return fragmentCache;
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
			world.reloadPlayers();
		}
		if (mapWindow != null) {
			mapWindow.reloadPlayerLayer();
		}
	}
}
