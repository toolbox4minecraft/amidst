package amidst.gui.menu;

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
import amidst.gui.MainWindow;
import amidst.gui.UpdatePrompt;
import amidst.gui.worldsurroundings.WorldSurroundings;
import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.player.Player;
import amidst.preferences.BiomeColorProfile;
import amidst.preferences.BiomeColorProfileSelection;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class Actions {
	private final Application application;
	private final MojangApi mojangApi;
	private final MainWindow mainWindow;
	private final AtomicReference<WorldSurroundings> worldSurroundings;
	private final UpdatePrompt updatePrompt;
	private final BiomeColorProfileSelection biomeColorProfileSelection;
	private final WorkerExecutor workerExecutor;

	@CalledOnlyBy(AmidstThread.EDT)
	public Actions(Application application, MojangApi mojangApi,
			MainWindow mainWindow,
			AtomicReference<WorldSurroundings> worldSurroundings,
			UpdatePrompt updatePrompt,
			BiomeColorProfileSelection biomeColorProfileSelection,
			WorkerExecutor workerExecutor) {
		this.application = application;
		this.mojangApi = mojangApi;
		this.mainWindow = mainWindow;
		this.worldSurroundings = worldSurroundings;
		this.updatePrompt = updatePrompt;
		this.biomeColorProfileSelection = biomeColorProfileSelection;
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
	private void newFromSeed(WorldSeed seed) {
		WorldType worldType = mainWindow.askForWorldType();
		if (worldType != null) {
			try {
				mainWindow.setWorld(mojangApi.createWorldFromSeed(seed,
						worldType));
			} catch (IllegalStateException e) {
				mainWindow.displayException(e);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void openWorldFile() {
		File file = mainWindow.askForMinecraftWorldFile();
		if (file != null) {
			try {
				mainWindow.setWorld(mojangApi.createWorldFromFile(file));
			} catch (Exception e) {
				mainWindow.displayException(e);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void switchProfile() {
		application.displayVersionSelectWindow();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void exit() {
		application.exitGracefully();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToCoordinate() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			String input = mainWindow.askForCoordinates();
			if (input != null) {
				CoordinatesInWorld coordinates = CoordinatesInWorld
						.tryParse(input);
				if (coordinates != null) {
					worldSurroundings.centerOn(coordinates);
				} else {
					Log.w("Invalid location entered, ignoring.");
					mainWindow.displayError("You entered an invalid location.");
				}
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToSpawn() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.centerOn(worldSurroundings.getSpawnWorldIcon());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToStronghold() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			WorldIcon stronghold = mainWindow.askForOptions("Go to",
					"Select Stronghold:",
					worldSurroundings.getStrongholdWorldIcons());
			if (stronghold != null) {
				worldSurroundings.centerOn(stronghold);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToPlayer() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			List<WorldIcon> playerWorldIcons = worldSurroundings
					.getPlayerWorldIcons();
			if (!playerWorldIcons.isEmpty()) {
				WorldIcon player = mainWindow.askForOptions("Go to",
						"Select player:", playerWorldIcons);
				if (player != null) {
					worldSurroundings.centerOn(player);
				}
			} else {
				mainWindow.displayError("There are no players in this world.");
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void savePlayerLocations() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.savePlayerLocations();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reloadPlayerLocations() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.loadPlayers(workerExecutor);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void howCanIMoveAPlayer() {
		mainWindow
				.displayMessage(
						"How can I move a player?",
						"If you load the world from a minecraft save folder, you can change the player locations.\n"
								+ "1. Scroll the map to and right-click on the new player location.\n"
								+ "2. Select the player you want to move to the new location.\n"
								+ "3. Enter the new player height (y-coordinate).\n"
								+ "4. Save player locations.\n\n"
								+ "WARNING: This will change the contents of the save folder, so there is a chance that the world gets corrupted.\n"
								+ "We try to minimize the risk by creating a backup of the changed file, before it is changed.\n"
								+ "If the backup fails, we will not write the changes.\n"
								+ "You can find the backup files in a sub folder of the world, named 'amidst_backup'.\n"
								+ "Especially, make sure to not have the world loaded in minecraft during this process.");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void copySeedToClipboard() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			String seed = "" + worldSurroundings.getWorldSeed().getLong();
			StringSelection selection = new StringSelection(seed);
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(selection, selection);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void saveCaptureImage() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			BufferedImage image = worldSurroundings.createCaptureImage();
			File file = mainWindow.askForCaptureImageSaveFile();
			if (file != null) {
				saveImageToFile(image, file);
			}
			image.flush();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectBiomeColorProfile(BiomeColorProfile profile) {
		biomeColorProfileSelection.setProfile(profile);
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.reloadBiomeLayer();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdates() {
		updatePrompt.check(mainWindow);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void viewLicense() {
		application.displayLicenseWindow();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void about() {
		mainWindow.displayMessage("About",
				"Advanced Minecraft Interfacing and Data/Structure Tracking (AMIDST)\n"
						+ "By Skidoodle (amidst.project@gmail.com)");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(int notches) {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.adjustZoom(notches);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(Point mousePosition, int notches) {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.adjustZoom(mousePosition, notches);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectWorldIcon(WorldIcon worldIcon) {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.selectWorldIcon(worldIcon);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void showPlayerPopupMenu(CoordinatesInWorld targetCoordinates,
			Component component, int x, int y) {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			if (worldSurroundings.canSavePlayerLocations()) {
				new MovePlayerPopupMenu(this,
						worldSurroundings.getMovablePlayerList(),
						targetCoordinates).show(component, x, y);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void movePlayer(Player player, CoordinatesInWorld targetCoordinates) {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			long currentHeight = player.getPlayerCoordinates().getY();
			String input = mainWindow.askForPlayerHeight(currentHeight);
			if (input != null) {
				player.moveTo(targetCoordinates,
						tryParseLong(input, currentHeight));
				worldSurroundings.reloadPlayerLayer();
				if (mainWindow
						.askToConfirm(
								"Save Player Location",
								"Do you want to save the player locations NOW? Make sure to not have the world opened in minecraft at the same time!")) {
					worldSurroundings.savePlayerLocations();
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
	private void saveImageToFile(BufferedImage image, File file) {
		try {
			ImageIO.write(image, "png", appendPNGFileExtensionIfNecessary(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private File appendPNGFileExtensionIfNecessary(File file) {
		String filename = file.toString();
		if (!filename.toLowerCase().endsWith(".png")) {
			filename += ".png";
		}
		return new File(filename);
	}
}
