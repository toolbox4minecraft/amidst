package amidst.gui.main;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.menu.MovePlayerPopupMenu;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.player.Player;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class Actions {
	private final Application application;
	private final MojangApi mojangApi;
	private final MainWindow mainWindow;
	private final AtomicReference<ViewerFacade> viewerFacade;
	private final BiomeProfileSelection biomeProfileSelection;
	private final WorkerExecutor workerExecutor;

	@CalledOnlyBy(AmidstThread.EDT)
	public Actions(
			Application application,
			MojangApi mojangApi,
			MainWindow mainWindow,
			AtomicReference<ViewerFacade> viewerFacade,
			BiomeProfileSelection biomeProfileSelection,
			WorkerExecutor workerExecutor) {
		this.application = application;
		this.mojangApi = mojangApi;
		this.mainWindow = mainWindow;
		this.viewerFacade = viewerFacade;
		this.biomeProfileSelection = biomeProfileSelection;
		this.workerExecutor = workerExecutor;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void newFromSeed() {
		WorldSeed seed = mainWindow.askForSeed();
		if (seed != null) {
			newFromSeed(seed);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void newFromRandom() {
		newFromSeed(WorldSeed.random());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void newFromSeed(WorldSeed worldSeed) {
		WorldType worldType = mainWindow.askForWorldType();
		if (worldType != null) {
			try {
				mainWindow.setWorld(mojangApi.createWorldFromSeed(worldSeed, worldType));
			} catch (IllegalStateException | MinecraftInterfaceException e) {
				e.printStackTrace();
				mainWindow.displayException(e);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void openSaveGame() {
		File file = mainWindow.askForSaveGame();
		if (file != null) {
			try {
				mainWindow.setWorld(mojangApi.createWorldFromSaveGame(file));
			} catch (IllegalStateException | MinecraftInterfaceException | IOException | MojangApiParsingException e) {
				e.printStackTrace();
				mainWindow.displayException(e);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void switchProfile() {
		application.displayProfileSelectWindow();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void exit() {
		application.exitGracefully();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToCoordinate() {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			String input = mainWindow.askForCoordinates();
			if (input != null) {
				CoordinatesInWorld coordinates = CoordinatesInWorld.tryParse(input);
				if (coordinates != null) {
					viewerFacade.centerOn(coordinates);
				} else {
					Log.w("Invalid location entered, ignoring.");
					mainWindow.displayError("You entered an invalid location.");
				}
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToSpawn() {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			viewerFacade.centerOn(viewerFacade.getSpawnWorldIcon());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToStronghold() {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			WorldIcon stronghold = mainWindow.askForOptions(
					"Go to",
					"Select Stronghold:",
					viewerFacade.getStrongholdWorldIcons());
			if (stronghold != null) {
				viewerFacade.centerOn(stronghold);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToPlayer() {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			List<WorldIcon> playerWorldIcons = viewerFacade.getPlayerWorldIcons();
			if (!playerWorldIcons.isEmpty()) {
				WorldIcon player = mainWindow.askForOptions("Go to", "Select player:", playerWorldIcons);
				if (player != null) {
					viewerFacade.centerOn(player);
				}
			} else {
				mainWindow.displayError("There are no players in this world.");
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void savePlayerLocations() {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			if (mainWindow.askToConfirmSaveGameManipulation()) {
				viewerFacade.savePlayerLocations();
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reloadPlayerLocations() {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			viewerFacade.loadPlayers(workerExecutor);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void howCanIMoveAPlayer() {
		mainWindow.displayMessage(
				"How can I move a player?",
				"If you load the world from a save game, you can change the player locations.\n"
						+ "1. Scroll the map to and right-click on the new player location, this opens a popup menu.\n"
						+ "2. Select the player you want to move to the new location.\n"
						+ "3. Enter the new player height (y-coordinate).\n" + "4. Save player locations.");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void copySeedToClipboard() {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			String seed = "" + viewerFacade.getWorldSeed().getLong();
			StringSelection selection = new StringSelection(seed);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void saveCaptureImage() {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			BufferedImage image = viewerFacade.createCaptureImage();
			String suggestedFilename = "screenshot_" + viewerFacade.getWorldType().getFilenameText() + "_"
					+ viewerFacade.getWorldSeed().getLong() + ".png";
			File file = mainWindow.askForCaptureImageSaveFile(suggestedFilename);
			if (file != null) {
				file = appendPNGFileExtensionIfNecessary(file);
				if (file.exists() && !file.isFile()) {
					mainWindow
							.displayError("Unable to write capture image, because the target exists but is not a file: "
									+ file.getAbsolutePath());
				} else if (!canWriteToFile(file)) {
					mainWindow.displayError("Unable to write capture image, because you have no writing permissions: "
							+ file.getAbsolutePath());
				} else if (!file.exists()
						|| mainWindow.askToConfirm("Replace file?", "File already exists. Do you want to replace it?\n"
								+ file.getAbsolutePath() + "")) {
					saveImageToFile(image, file);
				}
			}
			image.flush();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectBiomeProfile(BiomeProfile profile) {
		biomeProfileSelection.set(profile);
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			viewerFacade.reloadBackgroundLayer();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdates() {
		application.checkForUpdates(mainWindow);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void viewLicense() {
		application.displayLicenseWindow();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void about() {
		mainWindow.displayMessage("About", "Amidst - Advanced Minecraft Interfacing and Data/Structure Tracking\n\n"
				+ "Author: Skidoodle aka skiphs\n" + "Mail: toolbox4minecraft+amidst@gmail.com\n"
				+ "Project Page: https://github.com/toolbox4minecraft/amidst");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(int notches) {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			viewerFacade.adjustZoom(notches);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(Point mousePosition, int notches) {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			viewerFacade.adjustZoom(mousePosition, notches);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectWorldIcon(WorldIcon worldIcon) {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			viewerFacade.selectWorldIcon(worldIcon);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void showPlayerPopupMenu(CoordinatesInWorld targetCoordinates, Component component, int x, int y) {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			if (viewerFacade.canSavePlayerLocations()) {
				new MovePlayerPopupMenu(this, viewerFacade.getMovablePlayerList(), targetCoordinates).show(
						component,
						x,
						y);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void movePlayer(Player player, CoordinatesInWorld targetCoordinates) {
		ViewerFacade viewerFacade = this.viewerFacade.get();
		if (viewerFacade != null) {
			PlayerCoordinates currentCoordinates = player.getPlayerCoordinates();
			long currentHeight = currentCoordinates.getY();
			String input = mainWindow.askForPlayerHeight(currentHeight);
			if (input != null) {
				player.moveTo(targetCoordinates, tryParseLong(input, currentHeight), currentCoordinates.getDimension());
				viewerFacade.reloadPlayerLayer();
				if (mainWindow.askToConfirm("Save Player Locations", "Do you want to save the player locations?")) {
					if (mainWindow.askToConfirmSaveGameManipulation()) {
						viewerFacade.savePlayerLocations();
					}
				}
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private long tryParseLong(String text, long defaultValue) {
		try {
			return Long.parseLong(text);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean canWriteToFile(File file) {
		File parentFile = file.getParentFile();
		return file.canWrite() || (!file.exists() && parentFile != null && parentFile.canWrite());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void saveImageToFile(BufferedImage image, File file) {
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
			mainWindow.displayException(e);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private File appendPNGFileExtensionIfNecessary(File file) {
		String filename = file.getAbsolutePath();
		if (!filename.toLowerCase().endsWith(".png")) {
			filename += ".png";
		}
		return new File(filename);
	}
}
