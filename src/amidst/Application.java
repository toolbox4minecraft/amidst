package amidst;

import java.io.File;
import java.net.MalformedURLException;

import amidst.gui.CrashWindow;
import amidst.gui.LicenseWindow;
import amidst.gui.MapWindow;
import amidst.gui.UpdatePrompt;
import amidst.gui.version.VersionSelectWindow;
import amidst.logging.Log;
import amidst.map.FragmentManager;
import amidst.map.LayerContainer;
import amidst.map.SkinLoader;
import amidst.map.layer.BiomeLayer;
import amidst.map.layer.GridLayer;
import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.LiveLayer;
import amidst.map.layer.NetherFortressLayer;
import amidst.map.layer.OceanMonumentLayer;
import amidst.map.layer.PlayerLayer;
import amidst.map.layer.SlimeLayer;
import amidst.map.layer.SpawnLayer;
import amidst.map.layer.StrongholdLayer;
import amidst.map.layer.TempleLayer;
import amidst.map.layer.VillageLayer;
import amidst.minecraft.IMinecraftInterface;
import amidst.minecraft.Minecraft;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.remote.RemoteMinecraft;
import amidst.minecraft.world.World;
import amidst.utilities.SeedHistoryLogger;
import amidst.version.MinecraftProfile;

public class Application {
	private ThreadMaster threadMaster = new ThreadMaster(this);
	private SkinLoader skinLoader = new SkinLoader(threadMaster);
	private SeedHistoryLogger seedHistoryLogger = new SeedHistoryLogger();
	private UpdatePrompt updateManager = new UpdatePrompt();

	private VersionSelectWindow versionSelectWindow;
	private MapWindow mapWindow;

	private World world;

	private LayerContainer layerContainer;
	private FragmentManager fragmentManager;

	private void initLayerContainer() {
		IconLayer playerLayer = new PlayerLayer();
		ImageLayer biomeLayer = new BiomeLayer(0);
		ImageLayer[] imageLayers = { biomeLayer, new SlimeLayer(1) };
		LiveLayer[] liveLayers = { new GridLayer() };
		IconLayer[] iconLayers = { new VillageLayer(),
				new OceanMonumentLayer(), new StrongholdLayer(),
				new TempleLayer(), new SpawnLayer(), new NetherFortressLayer(),
				playerLayer };
		layerContainer = new LayerContainer(playerLayer, biomeLayer,
				imageLayers, liveLayers, iconLayers);
	}

	private void initFragmentManager() {
		fragmentManager = new FragmentManager(getLayerContainer());
	}

	public void displayVersionSelectWindow() {
		setVersionSelectWindow(new VersionSelectWindow(this));
		setMapWindow(null);
	}

	public void displayMapWindow(RemoteMinecraft minecraftInterface) {
		displayMapWindow(minecraftInterface);
	}

	public void displayMapWindow(MinecraftProfile profile) {
		Util.setProfileDirectory(profile.getGameDir());
		displayMapWindow(createLocalMinecraftInterface(profile.getJarFile()));
	}

	public void displayMapWindow(String jarFile, String gameDirectory) {
		Util.setProfileDirectory(gameDirectory);
		displayMapWindow(createLocalMinecraftInterface(new File(jarFile)));
	}

	private void displayMapWindow(IMinecraftInterface minecraftInterface) {
		MinecraftUtil.setBiomeInterface(minecraftInterface);
		setMapWindow(new MapWindow(this));
		setVersionSelectWindow(null);
	}

	private IMinecraftInterface createLocalMinecraftInterface(File jarFile) {
		try {
			return new Minecraft(jarFile).createInterface();
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
		Options.instance.world = world;
		if (world != null) {
			seedHistoryLogger.log(world.getSeed());
			mapWindow.worldChanged();
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

	public FragmentManager getFragmentManager() {
		if (fragmentManager == null) {
			initFragmentManager();
		}
		return fragmentManager;
	}

	public LayerContainer getLayerContainer() {
		if (layerContainer == null) {
			initLayerContainer();
		}
		return layerContainer;
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
			mapWindow.tick();
		}
	}

	public void tickFragmentLoader() {
		if (fragmentManager != null) {
			fragmentManager.tick();
		}
	}
}
