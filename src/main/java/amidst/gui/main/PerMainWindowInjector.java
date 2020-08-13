package amidst.gui.main;

import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;

import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.FeatureToggles;
import amidst.dependency.injection.Factory3;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.export.BiomeExporter;
import amidst.gui.export.BiomeExporterDialog;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.menu.AmidstMenuBuilder;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.seedsearcher.SeedSearcher;
import amidst.gui.seedsearcher.SeedSearcherWindow;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.world.World;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.threading.ThreadMaster;

@NotThreadSafe
public class PerMainWindowInjector {
	@CalledOnlyBy(AmidstThread.EDT)
	private static String createVersionString(AmidstMetaData metadata, RunningLauncherProfile runningLauncherProfile) {
		return new StringBuilder()
				.append(metadata.getVersion().createLongVersionString())
				.append(" - Selected Profile: ")
				.append(runningLauncherProfile.getLauncherProfile().getProfileName())
				.append(" - Minecraft Version ")
				.append(runningLauncherProfile.getLauncherProfile().getVersionName())
				.append(" (recognised: ")
				.append(runningLauncherProfile.getRecognisedVersion().getName())
				.append(")")
				.toString();
	}

	private final Factory3<World, BiomeExporterDialog, Actions, ViewerFacade> viewerFacadeFactory;
	private final String versionString;
	private final JFrame frame;
	private final Container contentPane;
	private final AtomicReference<ViewerFacade> viewerFacadeReference;
	private final MainWindowDialogs dialogs;
	private final WorldSwitcher worldSwitcher;
	private final SeedSearcher seedSearcher;
	private final SeedSearcherWindow seedSearcherWindow;
	private final BiomeExporter biomeExporter;
	private final BiomeExporterDialog biomeExporterDialog;
	private final Actions actions;
	private final AmidstMenu menuBar;
	private final MainWindow mainWindow;

	@CalledOnlyBy(AmidstThread.EDT)
	public PerMainWindowInjector(
			Application application,
			AmidstMetaData metadata,
			AmidstSettings settings,
			MinecraftInstallation minecraftInstallation,
			RunningLauncherProfile runningLauncherProfile,
			BiomeProfileDirectory biomeProfileDirectory,
			Factory3<World, BiomeExporterDialog, Actions, ViewerFacade> viewerFacadeFactory,
			ThreadMaster threadMaster) {
		this.viewerFacadeFactory = viewerFacadeFactory;
		this.versionString = createVersionString(metadata, runningLauncherProfile);
		this.frame = new JFrame();
		frame.addComponentListener(new MultiMonitorFixer(frame));
		this.contentPane = frame.getContentPane();
		this.viewerFacadeReference = new AtomicReference<>();
		this.dialogs = new MainWindowDialogs(settings, runningLauncherProfile, frame);
		this.worldSwitcher = new WorldSwitcher(
				minecraftInstallation,
				runningLauncherProfile,
				this::createViewerFacade,
				threadMaster,
				frame,
				contentPane,
				viewerFacadeReference,
				dialogs,
				this::getMenuBar);
		if (FeatureToggles.SEED_SEARCH) {
			this.seedSearcher = new SeedSearcher(
					dialogs,
					runningLauncherProfile.createSilentPlayerlessCopy(),
					threadMaster.getWorkerExecutor());
			this.seedSearcherWindow = new SeedSearcherWindow(metadata, dialogs, worldSwitcher, seedSearcher);
		} else {
			this.seedSearcher = null;
			this.seedSearcherWindow = null;
		}
		this.biomeExporter = new BiomeExporter(threadMaster.getWorkerExecutor());
		this.biomeExporterDialog = new BiomeExporterDialog(biomeExporter, frame, settings.biomeProfileSelection, this::getMenuBar, settings.lastBiomeExportPath);
		this.actions = new Actions(
				application,
				dialogs,
				worldSwitcher,
				seedSearcherWindow,
				biomeExporterDialog,
				viewerFacadeReference::get,
				settings.biomeProfileSelection,
				settings.lastBiomeExportPath);
		this.menuBar = new AmidstMenuBuilder(settings, actions, biomeProfileDirectory).construct();
		this.mainWindow = new MainWindow(frame, worldSwitcher, viewerFacadeReference::get, seedSearcherWindow, biomeExporterDialog);
		this.mainWindow.initializeFrame(metadata, versionString, actions, menuBar, runningLauncherProfile.getInitialWorldOptions());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ViewerFacade createViewerFacade(World world) {
		return viewerFacadeFactory.create(world, biomeExporterDialog, actions);
	}

	/**
	 * This only exists to break the cyclic dependency between {@link #menuBar},
	 * {@link #actions}, {@link #worldSwitcher}, aswell as the cyclic dependency
	 * between {@link #menuBar}, {@link #actions}, {@link #biomeExporterDialog}.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	private AmidstMenu getMenuBar() {
		return this.menuBar;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow getMainWindow() {
		return mainWindow;
	}
}
