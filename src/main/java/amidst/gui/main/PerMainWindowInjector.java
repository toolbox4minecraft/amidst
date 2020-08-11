package amidst.gui.main;

import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
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
import amidst.logging.AmidstLogger;
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
		// patch for multiple monitors
		frame.addComponentListener(new ComponentListener() {
			private final MethodHandle setGraphicsConfiguration;
			private boolean errorPrinted = false;
			
			{
				MethodHandle mh1 = null;
				
				try {
					Method m1 = Window.class.getDeclaredMethod("setGraphicsConfiguration", GraphicsConfiguration.class);
					m1.setAccessible(true);
					mh1 = MethodHandles.lookup().unreflect(m1);
					mh1 = mh1.asType(MethodType.methodType(void.class, JFrame.class, GraphicsConfiguration.class)); // change to allow invokeExact
				} catch (NoSuchMethodException | IllegalAccessException e) {
					AmidstLogger.error(e, "Unable to get setGraphicsConfiguration method");
				}
				
				this.setGraphicsConfiguration = mh1;
			}

			public void componentResized(ComponentEvent e) { updateGC(); }
			public void componentMoved(ComponentEvent e) { updateGC(); }
			public void componentShown(ComponentEvent e) { updateGC(); }
			public void componentHidden(ComponentEvent e) { updateGC(); }
			
			private void updateGC() {
				try {
					for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()){
						GraphicsConfiguration defaultConfig = gd.getDefaultConfiguration();
						if (!frame.getGraphicsConfiguration().equals(defaultConfig)) {
						    if (frame.getLocation().getX() >= defaultConfig.getBounds().getMinX() &&
						        frame.getLocation().getX() < defaultConfig.getBounds().getMaxX() &&
						        frame.getLocation().getY() >= defaultConfig.getBounds().getMinY() &&
						        frame.getLocation().getY() < defaultConfig.getBounds().getMaxY()) {
						    	setGraphicsConfiguration.invokeExact(frame, defaultConfig);
						    }
						}
					}
				} catch (Throwable t) {
					if(!errorPrinted) {
						AmidstLogger.error(t, "Unable to set GraphicsConfiguration");
						errorPrinted = true;
					}
				}
			}
			
		});
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
