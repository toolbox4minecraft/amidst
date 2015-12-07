package amidst.gui.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;

import amidst.Application;
import amidst.gui.MainWindow;
import amidst.logging.Log;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.WorldType;
import amidst.minecraft.world.icon.WorldIcon;

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
		application.getWorld().getMovablePlayerList().savePlayerLocations();
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
		WorldIcon stronghold = mainWindow.askForOptions("Go to",
				"Select Stronghold:", application.getWorld()
						.getStrongholdWorldIcons());
		if (stronghold != null) {
			mainWindow.centerWorldOn(stronghold.getCoordinates());
		}
	}

	public void gotoCoordinate() {
		CoordinatesInWorld coordinates = mainWindow.askForCoordinates();
		if (coordinates != null) {
			mainWindow.centerWorldOn(coordinates);
		} else {
			mainWindow.displayMessage("You entered an invalid location.");
			Log.w("Invalid location entered, ignoring.");
		}
	}

	public void gotoPlayer() {
		if (!application.getWorld().getMovablePlayerList().isEmpty()) {
			WorldIcon player = mainWindow.askForOptions("Go to",
					"Select player:", application.getWorld()
							.getPlayerWorldIcons());
			if (player != null) {
				mainWindow.centerWorldOn(player.getCoordinates());
			}
		} else {
			mainWindow.displayMessage("There are no players in this world.");
		}
	}

	public void capture() {
		File file = mainWindow.askForScreenshotSaveFile();
		if (file != null) {
			mainWindow.capture(file);
		}
	}

	public void copySeedToClipboard() {
		String seed = "" + application.getWorld().getSeed();
		StringSelection selection = new StringSelection(seed);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(selection, selection);
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
