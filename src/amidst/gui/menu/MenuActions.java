package amidst.gui.menu;

import amidst.Application;
import amidst.gui.MainWindow;

public class MenuActions {
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
