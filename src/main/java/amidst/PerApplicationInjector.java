package amidst;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerBuilder;
import amidst.gui.export.BiomeExporterDialog;
import amidst.gui.license.LicenseWindow;
import amidst.gui.main.*;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.gui.main.viewer.PerViewerFacadeInjector;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.main.viewer.Zoom;
import amidst.gui.profileselect.ProfileSelectWindow;
import amidst.mojangapi.LauncherProfileRunner;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.*;
import amidst.mojangapi.world.SeedHistoryLogger;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldBuilder;
import amidst.parsing.FormatException;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.threading.ThreadMaster;

import java.io.IOException;
import java.util.Optional;

@NotThreadSafe
public class PerApplicationInjector {

	private final ThreadMaster threadMaster = new ThreadMaster();
	private final LayerBuilder layerBuilder = new LayerBuilder();
	private final BiomeSelection biomeSelection = new BiomeSelection();

	private final AmidstMetaData metadata;
	private final AmidstSettings settings;
	private final MinecraftInstallation minecraftInstallation;
	private final LauncherProfileRunner launcherProfileRunner;
	private final BiomeProfileDirectory biomeProfileDirectory;
	private final VersionListProvider versionListProvider;
	private final Zoom zoom;
	private final FragmentManager fragmentManager;
	private final Application application;

	@CalledOnlyBy(AmidstThread.EDT)
	public PerApplicationInjector(CommandLineParameters parameters, AmidstMetaData metadata, AmidstSettings settings)
			throws DotMinecraftDirectoryNotFoundException,
			FormatException,
			IOException {
		this.metadata = metadata;
		this.settings = settings;

		minecraftInstallation = MinecraftInstallation.newLocalMinecraftInstallation(parameters.dotMinecraftDirectory);

		Optional<LauncherProfile> preferredLauncherProfile = parameters.getInitialLauncherProfile(minecraftInstallation);

		WorldBuilder worldBuilder = new WorldBuilder(new PlayerInformationCache(), SeedHistoryLogger.from(parameters.seedHistoryFile));
		launcherProfileRunner = new LauncherProfileRunner(worldBuilder, parameters.getInitialWorldOptions());
		biomeProfileDirectory = BiomeProfileDirectory.create(parameters.biomeProfilesDirectory);
		versionListProvider = VersionListProvider.createLocalAndStartDownloadingRemote(threadMaster.getWorkerExecutor());
		zoom = new Zoom(settings.maxZoom);
		fragmentManager = new FragmentManager(layerBuilder.getConstructors(), layerBuilder.getNumberOfLayers(), settings.threads);

		this.application = new Application(
				preferredLauncherProfile,
				launcherProfileRunner,
				this::createNoisyUpdatePrompt,
				this::createSilentUpdatePrompt,
				this::createMainWindow,
				this::createProfileSelectWindow,
				this::createLicenseWindow);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private UpdatePrompt createNoisyUpdatePrompt(MainWindowDialogs dialogs) {
		return UpdatePrompt.from(metadata.getVersion(), threadMaster.getWorkerExecutor(), dialogs, false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private UpdatePrompt createSilentUpdatePrompt() {
		return UpdatePrompt.from(metadata.getVersion(), threadMaster.getWorkerExecutor(), null, true);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private MainWindow createMainWindow(RunningLauncherProfile runningLauncherProfile) {
		return new PerMainWindowInjector(
				application,
				metadata,
				settings,
				minecraftInstallation,
				runningLauncherProfile,
				biomeProfileDirectory,
				this::createViewerFacade,
				threadMaster).getMainWindow();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ProfileSelectWindow createProfileSelectWindow() {
		return new ProfileSelectWindow(
				application,
				metadata,
				threadMaster.getWorkerExecutor(),
				versionListProvider,
				minecraftInstallation,
				launcherProfileRunner,
				settings);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private LicenseWindow createLicenseWindow() {
		return new LicenseWindow(metadata);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ViewerFacade createViewerFacade(World world, BiomeExporterDialog biomeExporterDialog, Actions actions) {
		return new PerViewerFacadeInjector(
				settings,
				threadMaster.getWorkerExecutor(),
				zoom,
				layerBuilder,
				fragmentManager,
				biomeExporterDialog,
				biomeSelection,
				world,
				actions).getViewerFacade();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Application getApplication() {
		return application;
	}
}
