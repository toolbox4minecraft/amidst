package amidst;

import java.io.FileNotFoundException;
import java.util.prefs.Preferences;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerBuilder;
import amidst.gui.license.LicenseWindow;
import amidst.gui.main.MainWindow;
import amidst.gui.main.UpdatePrompt;
import amidst.gui.main.viewer.ViewerFacadeBuilder;
import amidst.gui.profileselect.ProfileSelectWindow;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.MojangApiBuilder;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.SeedHistoryLogger;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.player.PlayerInformationCache;
import amidst.threading.ThreadMaster;

@NotThreadSafe
public class Application {
	private final CommandLineParameters parameters;
	private final AmidstMetaData metadata;
	private final Settings settings;
	private final MojangApi mojangApi;
	private final ViewerFacadeBuilder viewerFacadeBuilder;
	private final ThreadMaster threadMaster;

	private volatile ProfileSelectWindow profileSelectWindow;
	private volatile MainWindow mainWindow;

	@CalledOnlyBy(AmidstThread.EDT)
	public Application(CommandLineParameters parameters)
			throws FileNotFoundException,
			LocalMinecraftInterfaceCreationException {
		this.parameters = parameters;
		this.metadata = createMetadata();
		this.settings = createSettings();
		this.mojangApi = createMojangApi();
		this.viewerFacadeBuilder = createViewerFacadeBuilder();
		this.threadMaster = createThreadMaster();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private AmidstMetaData createMetadata() {
		return AmidstMetaData.from(
				ResourceLoader.getProperties("/amidst/metadata.properties"),
				ResourceLoader.getImage("/amidst/icon.png"));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Settings createSettings() {
		return new Settings(Preferences.userNodeForPackage(Amidst.class));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private MojangApi createMojangApi() throws FileNotFoundException,
			LocalMinecraftInterfaceCreationException {
		return new MojangApiBuilder(new WorldBuilder(
				new PlayerInformationCache(), new SeedHistoryLogger(
						parameters.historyPath)), parameters.minecraftPath,
				parameters.minecraftLibraries, parameters.minecraftJar,
				parameters.minecraftJson).construct();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ViewerFacadeBuilder createViewerFacadeBuilder() {
		return new ViewerFacadeBuilder(settings, new LayerBuilder(settings));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ThreadMaster createThreadMaster() {
		return new ThreadMaster();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void run() {
		checkForUpdatesSilently();
		if (mojangApi.canCreateWorld()) {
			displayMainWindow();
		} else {
			displayProfileSelectWindow();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdates(MainWindow mainWindow) {
		UpdatePrompt.from(metadata.getVersion(),
				threadMaster.getWorkerExecutor(), mainWindow, false).check();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdatesSilently() {
		UpdatePrompt.from(metadata.getVersion(),
				threadMaster.getWorkerExecutor(), null, true).check();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayMainWindow() {
		setMainWindow(new MainWindow(this, metadata, settings, mojangApi,
				viewerFacadeBuilder, threadMaster));
		setProfileSelectWindow(null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayProfileSelectWindow() {
		setProfileSelectWindow(new ProfileSelectWindow(this, metadata,
				threadMaster.getWorkerExecutor(), mojangApi, settings));
		setMainWindow(null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setProfileSelectWindow(ProfileSelectWindow profileSelectWindow) {
		disposeProfileSelectWindow();
		this.profileSelectWindow = profileSelectWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setMainWindow(MainWindow mainWindow) {
		disposeMainWindow();
		this.mainWindow = mainWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void disposeProfileSelectWindow() {
		ProfileSelectWindow profileSelectWindow = this.profileSelectWindow;
		if (profileSelectWindow != null) {
			profileSelectWindow.dispose();
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
		new LicenseWindow(metadata);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void exitGracefully() {
		dispose();
		System.exit(0);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		setProfileSelectWindow(null);
		setMainWindow(null);
	}
}
