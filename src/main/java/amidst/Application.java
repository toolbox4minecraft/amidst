package amidst;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerBuilder;
import amidst.gui.license.LicenseWindow;
import amidst.gui.main.MainWindow;
import amidst.gui.main.MainWindowDialogs;
import amidst.gui.main.UpdatePrompt;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.gui.main.viewer.Zoom;
import amidst.gui.profileselect.ProfileSelectWindow;
import amidst.mojangapi.LauncherProfileRunner;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.*;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.SeedHistoryLogger;
import amidst.mojangapi.world.WorldBuilder;
import amidst.parsing.FormatException;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.threading.ThreadMaster;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@NotThreadSafe
public class Application {
	private final LauncherProfileRunner launcherProfileRunner;

	private volatile ProfileSelectWindow profileSelectWindow;
	private volatile MainWindow mainWindow;
	private volatile Optional<LauncherProfile> selectedLauncherProfile;

	private final ThreadMaster threadMaster = new ThreadMaster();
	private final LayerBuilder layerBuilder = new LayerBuilder();
	private final BiomeSelection biomeSelection = new BiomeSelection();

	private final AmidstMetaData metadata;
	private final AmidstSettings settings;
	private final MinecraftInstallation minecraftInstallation;
	private final BiomeProfileDirectory biomeProfileDirectory;
	private final VersionListProvider versionListProvider;
	private final Zoom zoom;
	private final FragmentManager fragmentManager;

	private final List<Version> versions;

	@CalledOnlyBy(AmidstThread.EDT)
	public Application(CommandLineParameters parameters, AmidstMetaData metadata, AmidstSettings settings) throws FormatException, IOException {
		this.metadata = metadata;
		this.settings = settings;

		minecraftInstallation = MinecraftInstallation.newLocalMinecraftInstallation(parameters.dotMinecraftDirectory);

		selectedLauncherProfile = parameters.getInitialLauncherProfile(minecraftInstallation);

		WorldBuilder worldBuilder = new WorldBuilder(new PlayerInformationCache(), SeedHistoryLogger.from(parameters.seedHistoryFile));
		launcherProfileRunner = new LauncherProfileRunner(worldBuilder, parameters.getInitialWorldOptions());
		biomeProfileDirectory = BiomeProfileDirectory.create(parameters.biomeProfilesDirectory);
		versionListProvider = new VersionListProvider(threadMaster.getWorkerExecutor());
		versions = Version.newLocalVersionList();
		zoom = new Zoom(settings.maxZoom);
		fragmentManager = new FragmentManager(layerBuilder.getConstructors(), layerBuilder.getNumberOfLayers(), settings.threads);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void run() throws MinecraftInterfaceCreationException {
		checkForUpdatesSilently();
		if (selectedLauncherProfile.isPresent()) {
			displayMainWindow(launcherProfileRunner.run(selectedLauncherProfile.get()));
		} else {
			displayProfileSelectWindow();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdates(MainWindowDialogs dialogs) {
		UpdatePrompt.from(metadata.getVersion(), threadMaster.getWorkerExecutor(), dialogs, false).check();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdatesSilently() {
		UpdatePrompt.from(metadata.getVersion(), threadMaster.getWorkerExecutor(), null, true).check();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow displayMainWindow(RunningLauncherProfile runningLauncherProfile) {
		selectedLauncherProfile = Optional.of(runningLauncherProfile.getLauncherProfile());
		MainWindow m = new MainWindow(
				this,
				metadata,
				settings,
				minecraftInstallation,
				runningLauncherProfile,
				biomeProfileDirectory,
				zoom,
				layerBuilder,
				fragmentManager,
				biomeSelection,
				threadMaster);
		setMainWindow(m);
		setProfileSelectWindow(null);
		return mainWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public ProfileSelectWindow displayProfileSelectWindow() {
		ProfileSelectWindow window = new ProfileSelectWindow(
				this,
				metadata,
				threadMaster.getWorkerExecutor(),
				versions,
				versionListProvider,
				minecraftInstallation,
				launcherProfileRunner,
				settings);
		setProfileSelectWindow(window);
		setMainWindow(null);
		return profileSelectWindow;
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


	@CalledOnlyBy(AmidstThread.EDT)
	public void restart() {
		dispose();
		SwingUtilities.invokeLater(() -> {
			try {
				run();
			} catch(MinecraftInterfaceCreationException e) {
				throw new RuntimeException("Unexpected exception while restarting Amidst", e);
			}
		});
	}
}
