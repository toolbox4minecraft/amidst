package amidst.gui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.JFrame;

import amidst.AmidstMetaData;
import amidst.FeatureToggles;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.export.BiomeExporterDialog;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.seedsearcher.SeedSearcherWindow;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.WorldOptions;

@NotThreadSafe
public class MainWindow {
	private static final Dimension WINDOW_DIMENSIONS = new Dimension(1000, 800);
	
	private final JFrame frame;
	private final WorldSwitcher worldSwitcher;
	private final Supplier<ViewerFacade> viewerFacadeSupplier;
	private final SeedSearcherWindow seedSearcherWindow;
	private final BiomeExporterDialog biomeExporterDialog;

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow(JFrame frame, WorldSwitcher worldSwitcher, Supplier<ViewerFacade> viewerFacadeSupplier,
			SeedSearcherWindow seedSearcherWindow, BiomeExporterDialog biomeExporterDialog) {
		this.frame = frame;
		this.worldSwitcher = worldSwitcher;
		this.viewerFacadeSupplier = viewerFacadeSupplier;
		this.seedSearcherWindow = seedSearcherWindow;
		this.biomeExporterDialog = biomeExporterDialog;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void initializeFrame(AmidstMetaData metadata, String versionString, Actions actions, AmidstMenu menuBar,
				 Optional<WorldOptions> initialWorldOptions) {
		frame.setSize(WINDOW_DIMENSIONS);
		frame.setIconImages(metadata.getIcons());
		frame.setTitle(versionString);
		frame.setJMenuBar(menuBar.getMenuBar());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				actions.exit();
			}
		});
		frame.setVisible(true);
		worldSwitcher.clearWorld();
		initialWorldOptions.ifPresent(options -> {
            AmidstLogger.info("Setting initial world options to [" + options.getWorldSeed().getLabel() + ", World Type: " + options.getWorldType() + "]");
            worldSwitcher.displayWorld(options);
		});
	}

	public WorldSwitcher getWorldSwitcher() {
		return worldSwitcher;
	}

	public ViewerFacade getViewerFacade() {
		return viewerFacadeSupplier.get();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		biomeExporterDialog.dispose();
		worldSwitcher.clearWorld();
		if (FeatureToggles.SEED_SEARCH) {
			seedSearcherWindow.dispose();
		}
		frame.dispose();
	}
}
