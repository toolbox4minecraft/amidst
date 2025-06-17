package amidst.gui.main;

import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.FeatureToggles;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerBuilder;
import amidst.gui.export.BiomeExporter;
import amidst.gui.export.BiomeExporterDialog;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.menu.AmidstMenuBuilder;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.main.viewer.Zoom;
import amidst.gui.seedsearcher.SeedSearcher;
import amidst.gui.seedsearcher.SeedSearcherWindow;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.threading.ThreadMaster;
import amidst.util.SwingUtils;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main application window, showing the world map.
 */
@NotThreadSafe
public class MainWindow {

	/**
	 * The default window size.
	 */
	private static final Dimension WINDOW_DIMENSIONS = new Dimension(1000, 800);

	/**
	 * The JFrame that is the main window.
	 */
	private final JFrame frame;

	private final WorldSwitcher worldSwitcher;
	private final AtomicReference<ViewerFacade> viewerFacadeReference = new AtomicReference<>();
	private final SeedSearcherWindow seedSearcherWindow;
	private final BiomeExporterDialog biomeExporterDialog;

	/**
	 * Creates and shows the main application window.
	 *
	 * @param application
	 * @param metadata
	 * @param settings
	 * @param minecraftInstallation
	 * @param runningLauncherProfile
	 * @param biomeProfileDirectory
	 * @param zoom
	 * @param layerBuilder
	 * @param fragmentManager
	 * @param biomeSelection
	 * @param threadMaster
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow(Application application,
					  AmidstMetaData metadata,
					  AmidstSettings settings,
					  MinecraftInstallation minecraftInstallation,
					  RunningLauncherProfile runningLauncherProfile,
					  BiomeProfileDirectory biomeProfileDirectory,
					  Zoom zoom,
					  LayerBuilder layerBuilder,
					  FragmentManager fragmentManager,
					  BiomeSelection biomeSelection,
					  ThreadMaster threadMaster) {
		frame = new JFrame();
		Container contentPane = frame.getContentPane();

		MainWindowDialogs dialogs = new MainWindowDialogs(settings, runningLauncherProfile, frame);
		AtomicReference<AmidstMenu> menuBarReference = new AtomicReference<>();
		AtomicReference<Actions> actionsReference = new AtomicReference<>();

		if (FeatureToggles.SEED_SEARCH) {
			SeedSearcher seedSearcher = new SeedSearcher(
					dialogs,
					runningLauncherProfile.createSilentPlayerlessCopy(),
					threadMaster.getWorkerExecutor());
			seedSearcherWindow = new SeedSearcherWindow(metadata, dialogs, worldSwitcher, seedSearcher);
		} else {
			seedSearcherWindow = null;
		}

		biomeExporterDialog = new BiomeExporterDialog(new BiomeExporter(threadMaster.getWorkerExecutor()), frame, settings.biomeProfileSelection, menuBarReference::get, settings.lastBiomeExportPath);

		worldSwitcher = new WorldSwitcher(
				minecraftInstallation,
				runningLauncherProfile,
				settings,
				zoom,
				layerBuilder,
				fragmentManager,
				biomeSelection,
				actionsReference::get,
				biomeExporterDialog,
				threadMaster,
				frame,
				contentPane,
				viewerFacadeReference,
				dialogs,
				menuBarReference::get);
		Actions actions = new Actions(
				application,
				dialogs,
				worldSwitcher,
				seedSearcherWindow,
				biomeExporterDialog,
				viewerFacadeReference::get,
				settings.biomeProfileSelection,
				settings.lastBiomeExportPath);
		actionsReference.set(actions);

		AmidstMenu menuBar = new AmidstMenuBuilder(settings, actions, biomeProfileDirectory).construct();
		menuBarReference.set(menuBar);

		frame.setSize(WINDOW_DIMENSIONS);
		frame.setIconImages(metadata.getIcons());
		frame.setTitle(createVersionString(metadata, runningLauncherProfile));
		frame.setJMenuBar(menuBar.getMenuBar());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				actions.exit();
			}
		});
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		worldSwitcher.clearWorld();

		runningLauncherProfile.getInitialWorldOptions().ifPresent(options -> {
			AmidstLogger.info("Setting initial world options to [" + options.getWorldSeed().getLabel() + ", World Type: " + options.getWorldType() + "]");
			worldSwitcher.displayWorld(options);
		});
	}

	public WorldSwitcher getWorldSwitcher() {
		return worldSwitcher;
	}

	public ViewerFacade getViewerFacade() {
		return viewerFacadeReference.get();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		biomeExporterDialog.dispose();
		worldSwitcher.clearWorld();
		if (FeatureToggles.SEED_SEARCH) {
			seedSearcherWindow.dispose();
		}		
		SwingUtils.destroyComponentTree(frame);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static String createVersionString(AmidstMetaData metadata, RunningLauncherProfile runningLauncherProfile) {
		return metadata.getVersion().createLongVersionString() +
				" - Selected Profile: " +
				runningLauncherProfile.getLauncherProfile().getProfileName() +
				" - Minecraft Version " +
				runningLauncherProfile.getLauncherProfile().getVersionName() +
				" (recognised: " +
				runningLauncherProfile.getRecognisedVersion().getName() +
				")";
	}
}
