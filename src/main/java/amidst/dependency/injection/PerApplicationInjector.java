package amidst.dependency.injection;

import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.CommandLineParameters;
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
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.SeedHistoryLogger;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.player.PlayerInformationCache;
import amidst.mojangapi.world.player.PlayerInformationCacheImpl;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.threading.ThreadMaster;

@NotThreadSafe
public class PerApplicationInjector {
	private final AmidstMetaData metadata;
	private final AmidstSettings settings;
	private final PlayerInformationCache playerInformationCache;
	private final SeedHistoryLogger seedHistoryLogger;
	private final WorldBuilder worldBuilder;
	private final MojangApi mojangApi;
	private final BiomeProfileDirectory biomeProfileDirectory;
	private final ThreadMaster threadMaster;
	private final LayerBuilder layerBuilder;
	private final ViewerFacadeBuilder viewerFacadeBuilder;
	private final Application application;

	@CalledOnlyBy(AmidstThread.EDT)
	public PerApplicationInjector(CommandLineParameters parameters, AmidstMetaData metadata, AmidstSettings settings)
			throws DotMinecraftDirectoryNotFoundException,
			LocalMinecraftInterfaceCreationException {
		this.metadata = metadata;
		this.settings = settings;
		this.playerInformationCache = new PlayerInformationCacheImpl();
		this.seedHistoryLogger = SeedHistoryLogger.from(parameters.seedHistoryFile);
		this.worldBuilder = new WorldBuilder(playerInformationCache, seedHistoryLogger);
		this.mojangApi = new MojangApiBuilder(worldBuilder, parameters).construct();
		this.biomeProfileDirectory = BiomeProfileDirectory.create(parameters.biomeProfilesDirectory);
		this.threadMaster = new ThreadMaster();
		this.layerBuilder = new LayerBuilder();
		this.viewerFacadeBuilder = new ViewerFacadeBuilder(settings, threadMaster.getWorkerExecutor(), layerBuilder);
		this.application = new Application(
				mojangApi,
				this::createNoisyUpdatePrompt,
				this::createSilentUpdatePrompt,
				this::createMainWindow,
				this::createProfileSelectWindow,
				this::createLicenseWindow);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Application getApplication() {
		return application;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private UpdatePrompt createNoisyUpdatePrompt(MainWindow mainWindow) {
		return UpdatePrompt.from(metadata.getVersion(), threadMaster.getWorkerExecutor(), mainWindow, false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private UpdatePrompt createSilentUpdatePrompt() {
		return UpdatePrompt.from(metadata.getVersion(), threadMaster.getWorkerExecutor(), null, true);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private MainWindow createMainWindow() {
		return new MainWindow(
				application,
				metadata,
				settings,
				mojangApi,
				biomeProfileDirectory,
				viewerFacadeBuilder,
				threadMaster);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ProfileSelectWindow createProfileSelectWindow() {
		return new ProfileSelectWindow(application, metadata, threadMaster.getWorkerExecutor(), mojangApi, settings);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private LicenseWindow createLicenseWindow() {
		return new LicenseWindow(metadata);
	}
}
