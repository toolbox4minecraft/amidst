package amidst.devtools;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import amidst.Amidst;
import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.CommandLineParameters;
import amidst.PerApplicationInjector;
import amidst.gui.main.MainWindow;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.parsing.FormatException;

public class ExternalMinecraftInterfaceCreator {
	private static final WorldOptions WORLD_OPTIONS = new WorldOptions(WorldSeed.fromSaveGame(123456),
			WorldType.DEFAULT);
	
	private final Semaphore fullyLoadedBarrier = new Semaphore(0);
	
	public ExternalMinecraftInterfaceCreator() throws DotMinecraftDirectoryNotFoundException {
		
	}
	
	public void run() throws FormatException, IOException {
		Application app = startAmidst();
		
		benchmarkOne(app);
	}
	
	private void benchmarkOne(Application app) {
		//dummy run + real run, only the data gathered on the real run counts
		for (int i = 0; i < 2; i++) {
			SwingUtilities.invokeLater(() -> {
				try {
					launchMainWindow(app);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(4);
				}
			});
			fullyLoadedBarrier.acquireUninterruptibly();
		}
	}
	
	private void launchMainWindow(Application app) throws MinecraftInterfaceCreationException {
		LauncherProfile launcherProfile = new LauncherProfile(null, null, null, null, false, "testing");
		MainWindow window = app.displayMainWindow(RunningLauncherProfile.from(WorldBuilder.createSilentPlayerless(), launcherProfile, Optional.of(WORLD_OPTIONS)));
		window.getWorldSwitcher().displayWorld(WORLD_OPTIONS);
		
		for (int i = 0; i < 10; i++) {
			window.getViewerFacade().adjustZoom(1);
		}
		window.getViewerFacade().centerOn(CoordinatesInWorld.origin());
		
		Timer timer = new Timer(1000, e -> {
		});
		timer.setRepeats(true);
		timer.setInitialDelay(1000);
		timer.addActionListener(e -> {
			if (window.getViewerFacade().isFullyLoaded()) {
				timer.stop();
				fullyLoadedBarrier.release();
			}
		});
		timer.start();
	}
	
	private Application startAmidst() throws FormatException, IOException {
		AmidstSettings settings = new AmidstSettings(Preferences.userNodeForPackage(getClass()));
		CommandLineParameters params = new CommandLineParameters();
		AmidstMetaData metadata = Amidst.createMetadata();
		return new PerApplicationInjector(params, metadata, settings).getApplication();
	}
}
