package amidst.gui.main;

import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;

import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.dependency.injection.Factory2;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.menu.AmidstMenuBuilder;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.seedsearcher.SeedSearcher;
import amidst.gui.seedsearcher.SeedSearcherWindow;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.world.World;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.threading.ThreadMaster;

@NotThreadSafe
public class PerMainWindowInjector {
	@CalledOnlyBy(AmidstThread.EDT)
	private static String createVersionString(AmidstMetaData metadata, MojangApi mojangApi) {
		return metadata.getVersion().createLongVersionString() + " - Selected Profile: " + mojangApi.getProfileName()
				+ " - Minecraft Version " + mojangApi.getVersionId() + " (recognised: "
				+ mojangApi.getRecognisedVersionName() + ")";
	}

	private final Factory2<World, Actions, ViewerFacade> viewerFacadeFactory;
	private final String versionString;
	private final JFrame frame;
	private final Container contentPane;
	private final AtomicReference<ViewerFacade> viewerFacadeReference;
	private final MainWindowDialogs dialogs;
	private final WorldSwitcher worldSwitcher;
	private final SeedSearcher seedSearcher;
	private final SeedSearcherWindow seedSearcherWindow;
	private final Actions actions;
	private final AmidstMenu menuBar;
	private final MainWindow mainWindow;

	@CalledOnlyBy(AmidstThread.EDT)
	public PerMainWindowInjector(
			Application application,
			AmidstMetaData metadata,
			AmidstSettings settings,
			MojangApi mojangApi,
			BiomeProfileDirectory biomeProfileDirectory,
			Factory2<World, Actions, ViewerFacade> viewerFacadeFactory,
			ThreadMaster threadMaster) {
		this.viewerFacadeFactory = viewerFacadeFactory;
		this.versionString = createVersionString(metadata, mojangApi);
		this.frame = new JFrame();
		this.contentPane = frame.getContentPane();
		this.viewerFacadeReference = new AtomicReference<>();
		this.dialogs = new MainWindowDialogs(settings, mojangApi, frame);
		this.worldSwitcher = new WorldSwitcher(
				mojangApi,
				this::createViewerFacade,
				threadMaster,
				frame,
				contentPane,
				viewerFacadeReference,
				dialogs,
				this::getMenuBar);
		this.seedSearcher = new SeedSearcher(dialogs, mojangApi, threadMaster.getWorkerExecutor());
		this.seedSearcherWindow = new SeedSearcherWindow(metadata, dialogs, worldSwitcher, seedSearcher);
		this.actions = new Actions(
				application,
				dialogs,
				worldSwitcher,
				seedSearcherWindow,
				viewerFacadeReference::get,
				settings.biomeProfileSelection);
		this.menuBar = new AmidstMenuBuilder(settings, actions, biomeProfileDirectory).construct();
		this.mainWindow = new MainWindow(frame, worldSwitcher, seedSearcherWindow);
		this.mainWindow.initializeFrame(metadata, versionString, actions, menuBar);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ViewerFacade createViewerFacade(World world) {
		return viewerFacadeFactory.create(world, actions);
	}

	/**
	 * This only exists to break the cyclic dependency between {@link #menuBar},
	 * {@link #actions} and {@link #worldSwitcher}.
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
