package amidst.gui.menu;

import java.io.File;

import amidst.Application;
import amidst.gui.MainWindow;
import amidst.minecraft.world.WorldType;

public class MenuActions {
	private static final String ABOUT_MESSAGE = "Advanced Minecraft Interfacing and Data/Structure Tracking (AMIDST)\n"
			+ "By Skidoodle (amidst.project@gmail.com)";

	private Application application;
	private MainWindow mainWindow;

	public MenuActions(Application application, MainWindow mainWindow) {
		this.application = application;
		this.mainWindow = mainWindow;
	}

	public void savePlayerLocations() {
		mainWindow.savePlayerLocations();
	}

	public void switchVersion() {
		application.displayVersionSelectWindow();
	}

	public void exit() {
		application.exitGracefully();
	}

	public void newFromSeed() {
		String seed = mainWindow.askForSeed();
		if (seed != null) {
			if (seed.isEmpty()) {
				newFromRandom();
			} else {
				WorldType worldType = mainWindow.askForWorldType();
				if (worldType != null) {
					application.setWorld(application.getMojangApi()
							.createWorldFromSeed(seed, worldType));
				}
			}
		}
	}

	public void newFromRandom() {
		WorldType worldType = mainWindow.askForWorldType();
		if (worldType != null) {
			application.setWorld(application.getMojangApi().createRandomWorld(
					worldType));
		}
	}

	public void newFromFileOrFolder() {
		File worldFile = mainWindow.askForMinecraftWorldFile();
		if (worldFile != null) {
			try {
				application.setWorld(application.getMojangApi()
						.createWorldFromFile(worldFile));
			} catch (Exception e) {
				mainWindow.displayException(e);
			}
		}
	}

	public void findStronghold() {
		mainWindow.findStronghold();
	}

	public void gotoCoordinate() {
		mainWindow.gotoCoordinate();
	}

	public void gotoPlayer() {
		mainWindow.gotoPlayer();
	}

	public void capture() {
		File file = mainWindow.askForScreenshotSaveFile();
		if (file != null) {
			mainWindow.capture(file);
		}
	}

	public void copySeedToClipboard() {
		mainWindow.copySeedToClipboard();
	}

	public void checkForUpdates() {
		application.checkForUpdates();
	}

	public void viewLicense() {
		application.displayLicenseWindow();
	}

	public void about() {
		mainWindow.displayMessage(ABOUT_MESSAGE);
	}
}
