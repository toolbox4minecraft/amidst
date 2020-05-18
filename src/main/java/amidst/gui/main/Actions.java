package amidst.gui.main;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.crash.CrashWindow;
import amidst.gui.export.BiomeExporter;
import amidst.gui.export.BiomeExporterDialog;
import amidst.gui.main.menu.MovePlayerPopupMenu;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.seedsearcher.SeedSearcherWindow;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.player.Player;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.settings.Setting;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.util.FileExtensionChecker;

@NotThreadSafe
public class Actions {
	private final Application application;
	private final MainWindowDialogs dialogs;
	private final WorldSwitcher worldSwitcher;
	private final SeedSearcherWindow seedSearcherWindow;
	private final BiomeExporterDialog biomeExporterDialog;
	private final Supplier<ViewerFacade> viewerFacadeSupplier;
	private final BiomeProfileSelection biomeProfileSelection;
	private final Setting<String> lastScreenshotPath;

	@CalledOnlyBy(AmidstThread.EDT)
	public Actions(
			Application application,
			MainWindowDialogs dialogs,
			WorldSwitcher worldSwitcher,
			SeedSearcherWindow seedSearcherWindow,
			BiomeExporterDialog biomeExporterDialog,
			Supplier<ViewerFacade> viewerFacadeSupplier,
			BiomeProfileSelection biomeProfileSelection,
			Setting<String> lastScreenshotPath) {
		this.application = application;
		this.dialogs = dialogs;
		this.worldSwitcher = worldSwitcher;
		this.seedSearcherWindow = seedSearcherWindow;
		this.biomeExporterDialog = biomeExporterDialog;
		this.viewerFacadeSupplier = viewerFacadeSupplier;
		this.biomeProfileSelection = biomeProfileSelection;
		this.lastScreenshotPath = lastScreenshotPath;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void newFromSeed() {
		WorldSeed seed = dialogs.askForSeed();
		if (seed != null) {
			newFromSeed(seed);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void newFromRandom() {
		biomeExporterDialog.dispose();
		newFromSeed(WorldSeed.random());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void newFromSeed(WorldSeed worldSeed) {
		WorldType worldType = dialogs.askForWorldType();
		if (worldType != null) {
			biomeExporterDialog.dispose();
			worldSwitcher.displayWorld(new WorldOptions(worldSeed, worldType));
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void searchForRandom() {
		seedSearcherWindow.show();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void openSaveGame() {
		Path file = dialogs.askForSaveGame();
		if (file != null) {
			biomeExporterDialog.dispose();
			worldSwitcher.displayWorld(file);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void openExportDialog() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.openExportDialog();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void switchProfile() {
		application.displayProfileSelectWindow();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void exit() {
		if (BiomeExporter.isExporterRunning()) {
			if (dialogs.askToConfirmYesNo("Continue?", "A biome image is still exporting. Are you sure you want to continue?")) {
				application.exitGracefully();
			}
		} else {
			application.exitGracefully();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToCoordinate() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			String input = dialogs.askForCoordinates();
			if (input != null) {
				CoordinatesInWorld coordinates = CoordinatesInWorld.tryParse(input);
				if (coordinates != null) {
					viewerFacade.centerOn(coordinates);
				} else {
					AmidstLogger.warn("Invalid location entered, ignoring.");
					dialogs.displayError("You entered an invalid location.");
				}
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToSpawn() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.centerOn(viewerFacade.getSpawnWorldIcon());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToStronghold() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			WorldIcon stronghold = dialogs
					.askForOptions("Go to", "Select Stronghold:", viewerFacade.getStrongholdWorldIcons());
			if (stronghold != null) {
				viewerFacade.centerOn(stronghold);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void goToPlayer() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			List<WorldIcon> playerWorldIcons = viewerFacade.getPlayerWorldIcons();
			if (!playerWorldIcons.isEmpty()) {
				WorldIcon player = dialogs.askForOptions("Go to", "Select player:", playerWorldIcons);
				if (player != null) {
					viewerFacade.centerOn(player);
				}
			} else {
				AmidstLogger.warn("There are no players in this world.");
				dialogs.displayError("There are no players in this world.");
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void zoomIn() {
		adjustZoom(-4);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void zoomOut() {
		adjustZoom(4);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void savePlayerLocations() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			if (dialogs.askToConfirmSaveGameManipulation()) {
				viewerFacade.savePlayerLocations();
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reloadPlayerLocations() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.loadPlayers();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void howCanIMoveAPlayer() {
		dialogs.displayInfo(
				"How can I move a player?",
				"If you load the world from a save game, you can change the player locations.\n"
						+ "1. Scroll the map to and right-click on the new player location, this opens a popup menu.\n"
						+ "2. Select the player you want to move to the new location.\n"
						+ "3. Enter the new player height (y-coordinate).\n" + "4. Save player locations.");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void copySeedToClipboard() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			String seed = "" + viewerFacade.getWorldOptions().getWorldSeed().getLong();
			StringSelection selection = new StringSelection(seed);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void takeScreenshot() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			WorldOptions worldOptions = viewerFacade.getWorldOptions();
			BufferedImage image = viewerFacade.createScreenshot();
			String suggestedFilename = "screenshot_" + worldOptions.getWorldType().getFilenameText() + "_"
					+ worldOptions.getWorldSeed().getLong() + ".png";
			String suggestedFile = Paths.get(lastScreenshotPath.get(), suggestedFilename).toString();
			Path file = dialogs.askForPNGSaveFile(suggestedFile);
			if (file != null) {
				file = appendFileExtensionIfNecessary(file, "png");
				boolean fileExists = Files.exists(file);
				if (fileExists && !Files.isRegularFile(file)) {
					String message = "Unable to write screenshot, because the target exists but is not a file: "
							+ file.toString();
					AmidstLogger.warn(message);
					dialogs.displayError(message);
				} else if (!canWriteToFile(file)) {
					String message = "Unable to write screenshot, because you have no writing permissions: "
							+ file.toString();
					AmidstLogger.warn(message);
					dialogs.displayError(message);
				} else if (!fileExists || dialogs.askToConfirmYesNo(
						"Replace file?",
						"File already exists. Do you want to replace it?\n" + file.toString() + "")) {
					lastScreenshotPath.set(file.toAbsolutePath().getParent().toString());
					saveImageToFile(image, file);
				}
			}
			image.flush();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectBiomeProfile(BiomeProfile profile) {
		biomeProfileSelection.set(profile);
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.reloadBackgroundLayer();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void createExampleProfile(BiomeProfileDirectory dir) {
		if (!dir.isValid()) {
			dialogs.displayError("Unable to find biome profile directory.");
		} else {
			Path path = dir.getRoot().resolve("example.json");
			if (BiomeProfile.createExampleProfile().save(path)) {
				dialogs.displayInfo("Amidst", "Example biome profile created at:\n" + path.toAbsolutePath().toString());
			} else {
				dialogs.displayError("Error creating example biome profile.");
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayLogMessages() {
		CrashWindow.showForInterest();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdates() {
		application.checkForUpdates(dialogs);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void viewLicense() {
		application.displayLicenseWindow();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void about() {
		dialogs.displayInfo(
				"About",
				"Amidst - Advanced Minecraft Interfacing and Data/Structure Tracking\n\n"
						+ "Author: Skidoodle aka skiphs\n" + "Mail: toolbox4minecraft+amidst@outlook.com\n"
						+ "Project Page: https://github.com/toolbox4minecraft/amidst");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void adjustZoom(int notches) {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.adjustZoom(notches);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(Point mousePosition, int notches) {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.adjustZoom(mousePosition, notches);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectWorldIcon(WorldIcon worldIcon) {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.selectWorldIcon(worldIcon);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void showPlayerPopupMenu(CoordinatesInWorld targetCoordinates, Component component, int x, int y) {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			if (viewerFacade.canSavePlayerLocations()) {
				new MovePlayerPopupMenu(this, viewerFacade.getMovablePlayerList(), targetCoordinates)
						.show(component, x, y);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void movePlayer(Player player, CoordinatesInWorld targetCoordinates) {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			PlayerCoordinates currentCoordinates = player.getPlayerCoordinates();
			long currentHeight = currentCoordinates.getY();
			String input = dialogs.askForPlayerHeight(currentHeight);
			if (input != null) {
				player.moveTo(targetCoordinates, tryParseLong(input, currentHeight), currentCoordinates.getDimension());
				viewerFacade.reloadPlayerLayer();
				if (dialogs.askToConfirmYesNo("Save Player Locations", "Do you want to save the player locations?")) {
					if (dialogs.askToConfirmSaveGameManipulation()) {
						viewerFacade.savePlayerLocations();
					}
				}
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean tryChangeLookAndFeel(AmidstLookAndFeel lookAndFeel) {
		if (dialogs.askToConfirmYesNo("Changing Look & Feel",
				"Changing the look & feel will reload Amidst. Do you want to continue?")) {
			if (lookAndFeel.tryApply()) {
				application.restart();
				return true;
			} else {
				dialogs.displayError("An error occured while trying to switch to " + lookAndFeel);
			}
		}
		return false;
	}

	@CalledByAny
	public static long tryParseLong(String text, long defaultValue) {
		try {
			return Long.parseLong(text);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@CalledByAny
	public static boolean canWriteToFile(Path file) {
		Path parent = file.getParent();
		return Files.isWritable(file) || (!Files.exists(file) && parent != null && Files.isWritable(parent));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void saveImageToFile(BufferedImage image, Path file) {
		try {
			ImageIO.write(image, "png", new BufferedOutputStream(Files.newOutputStream(file)));
		} catch (IOException e) {
			AmidstLogger.warn(e);
			dialogs.displayError(e);
		}
	}

	@CalledByAny
	public static Path appendFileExtensionIfNecessary(Path file, String fileExtension) {
		String filename = file.toAbsolutePath().toString();
		if (!FileExtensionChecker.hasFileExtension(filename, fileExtension)) {
			filename += '.' + fileExtension;
		}
		return Paths.get(filename);
	}

}
