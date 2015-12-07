package amidst.gui.menu;

import java.util.concurrent.atomic.AtomicReference;

import amidst.Application;
import amidst.gui.MainWindow;
import amidst.gui.worldsurroundings.WorldSurroundings;
import amidst.preferences.BiomeColorProfile;
import amidst.preferences.BiomeColorProfileSelection;

public class MenuActions {
	private final Application application;
	private final MainWindow mainWindow;
	private final AtomicReference<WorldSurroundings> worldSurroundings;
	private final BiomeColorProfileSelection biomeColorProfileSelection;

	public MenuActions(Application application, MainWindow mainWindow,
			AtomicReference<WorldSurroundings> worldSurroundings,
			BiomeColorProfileSelection biomeColorProfileSelection) {
		this.application = application;
		this.mainWindow = mainWindow;
		this.worldSurroundings = worldSurroundings;
		this.biomeColorProfileSelection = biomeColorProfileSelection;
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
		mainWindow.newFromSeed();
	}

	public void newFromRandom() {
		mainWindow.newFromRandom();
	}

	public void newFromFileOrFolder() {
		mainWindow.newFromFileOrFolder();
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
		mainWindow.capture();
	}

	public void copySeedToClipboard() {
		mainWindow.copySeedToClipboard();
	}

	public void selectBiomeColorProfile(BiomeColorProfile profile) {
		biomeColorProfileSelection.setProfile(profile);
		mainWindow.reloadBiomeLayer();
	}

	public void checkForUpdates() {
		application.checkForUpdates();
	}

	public void viewLicense() {
		application.displayLicenseWindow();
	}

	public void about() {
		mainWindow.displayAboutMessage();
	}
}
