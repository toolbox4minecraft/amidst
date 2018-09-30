package amidst.gui.main;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import amidst.AmidstSettings;
import amidst.AmidstVersion;
import amidst.Application;
import amidst.FeatureToggles;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gameengineabstraction.GameEngineDetails;
import amidst.gameengineabstraction.GameEngineType;
import amidst.gui.crash.CrashWindow;
import amidst.gui.main.menu.MovePlayerPopupMenu;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.seedsearcher.SeedSearcherWindow;
import amidst.logging.AmidstLogger;
import amidst.minetest.world.mapgen.DefaultBiomes;
import amidst.minetest.world.mapgen.MapgenRelay;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.player.Player;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.settings.biomeprofile.BiomeAuthority;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.util.FileExtensionChecker;

@NotThreadSafe
public class Actions {
	private final Application application;
	private final MainWindowDialogs dialogs;
	private final WorldSwitcher worldSwitcher;
	private final SeedSearcherWindow seedSearcherWindow;
	private final Supplier<ViewerFacade> viewerFacadeSupplier;
	private final BiomeAuthority biomeAuthority;
	private final GameEngineDetails gameEngineDetails;
	private final AmidstVersion amidstVersion;
	private final MapgenRelay mapgenRelay;

	@CalledOnlyBy(AmidstThread.EDT)
	public Actions(
			Application application,
			MainWindowDialogs dialogs,
			WorldSwitcher worldSwitcher,
			SeedSearcherWindow seedSearcherWindow,
			Supplier<ViewerFacade> viewerFacadeSupplier,
			BiomeAuthority biomeAuthority,
			GameEngineDetails gameEngineDetails,
			AmidstVersion amidstVersion) {
		this.application = application;
		this.dialogs = dialogs;
		this.worldSwitcher = worldSwitcher;
		this.seedSearcherWindow = seedSearcherWindow;
		this.viewerFacadeSupplier = viewerFacadeSupplier;
		this.biomeAuthority = biomeAuthority;
		this.gameEngineDetails = gameEngineDetails;
		this.amidstVersion = amidstVersion;
		
		this.mapgenRelay = new MapgenRelay(worldSwitcher);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void newFromSeed() {
		WorldSeed seed = dialogs.askForSeed(gameEngineDetails.getType());
		if (seed != null) {
			newFromSeed(seed);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void newFromRandom() {
		newFromSeed(WorldSeed.random(gameEngineDetails.getType()));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void newFromSeed(WorldSeed worldSeed) {
		WorldType worldType = dialogs.askForWorldType();
		if (worldType != null) {
			
			if (worldType == WorldType.V6) {
				// V6 has a fixed biome, and rendering it with different biomes is
				// meaningless, unless the difference is only in the biome colours
				BiomeProfile v6profile = biomeAuthority.getBiomeProfileDirectory().getProfile(DefaultBiomes.BIOMEPROFILENAME_V6);
				if (v6profile != null) biomeAuthority.getBiomeProfileSelection().set(v6profile);
			} else if (DefaultBiomes.BIOMEPROFILENAME_V6.equals(biomeAuthority.getBiomeProfileSelection().getCurrentBiomeProfile().getName())) {
				// Probably previously viewed a v6 world - not a good profile for any other world type
				BiomeProfile mainProfile = biomeAuthority.getBiomeProfileDirectory().getProfile(DefaultBiomes.BIOMEPROFILENAME_MINETEST_GAME);
				if (mainProfile != null) biomeAuthority.getBiomeProfileSelection().set(mainProfile);
			}
			worldSwitcher.displayWorld(worldSeed, worldType, biomeAuthority.getBiomeProfileSelection());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void searchForRandom() {
		seedSearcherWindow.show();
	}

	
	@CalledOnlyBy(AmidstThread.EDT)
	public boolean canOpenSaveGame() {
		// "Temporary solution" until this feature is implemented in Minetest code
		return gameEngineDetails.getType() == GameEngineType.MINECRAFT;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	public void openSaveGame() {
		if (canOpenSaveGame()) {
			File file = dialogs.askForSaveGame();
			if (file != null) {
				worldSwitcher.displayWorld(file);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void export() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.export(dialogs.askForExportConfiguration());
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
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			String input = dialogs.askForCoordinates();
			if (input != null) {
				CoordinatesInWorld coordinates = CoordinatesInWorld.tryParse(input);
				if (coordinates != null) {
					CoordinatesInWorld amidstCoords = gameEngineDetails
							.getType()
							.getGameCoordinateSystem()
							.ConvertToRightHanded(coordinates);
					viewerFacade.centerOn(amidstCoords);
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
	public boolean canGoToStronghold() {
		// Strongholds make no sense outside of Minecraft
		return gameEngineDetails.getType() == GameEngineType.MINECRAFT;
	}	
	
	@CalledOnlyBy(AmidstThread.EDT)
	public void goToStronghold() {
		if (canGoToStronghold()) {
			ViewerFacade viewerFacade = viewerFacadeSupplier.get();
			if (viewerFacade != null) {
				WorldIcon stronghold = dialogs
						.askForOptions("Go to", "Select Stronghold:", viewerFacade.getStrongholdWorldIcons());
				if (stronghold != null) {
					viewerFacade.centerOn(stronghold);
				}
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
		adjustZoom(-1);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void zoomOut() {
		adjustZoom(1);
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
			String seed = viewerFacade.getWorldSeed().getLongAsString();
			StringSelection selection = new StringSelection(seed);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void takeScreenshot() {
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			BufferedImage image = viewerFacade.createScreenshot();
			String suggestedFilename = "screenshot_" + viewerFacade.getWorldType().getFilenameText() + "_"
					+ viewerFacade.getWorldSeed().getLongAsString() + ".png";
			File file = dialogs.askForScreenshotSaveFile(suggestedFilename);
			if (file != null) {
				file = appendPNGFileExtensionIfNecessary(file);
				if (file.exists() && !file.isFile()) {
					String message = "Unable to write screenshot, because the target exists but is not a file: "
							+ file.getAbsolutePath();
					AmidstLogger.warn(message);
					dialogs.displayError(message);
				} else if (!canWriteToFile(file)) {
					String message = "Unable to write screenshot, because you have no writing permissions: "
							+ file.getAbsolutePath();
					AmidstLogger.warn(message);
					dialogs.displayError(message);
				} else if (!file.exists() || dialogs.askToConfirmYesNo(
						"Replace file?",
						"File already exists. Do you want to replace it?\n" + file.getAbsolutePath() + "")) {
					saveImageToFile(image, file);
				}
			}
			image.flush();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectBiomeProfile(BiomeProfile profile, AmidstSettings settings) {
		biomeAuthority.getBiomeProfileSelection().set(profile);
		settings.lastBiomeProfile.set(profile.getName());
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			viewerFacade.reloadBackgroundLayer();
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

	/**
	 * This is more useful for Minetest than Minecraft, as Minetest has extensive
	 * modabble MapgenParams, which are exposed as the GeneratorOptions
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public void displayGeneratorOptions() {
		// 
		String generatorOptions = null;
		ViewerFacade viewerFacade = viewerFacadeSupplier.get();
		if (viewerFacade != null) {
			generatorOptions = viewerFacade.getGeneratorOptions();			
		}
		if (generatorOptions != null && generatorOptions.length() > 0) {
			dialogs.displayMonospaceText("World mapgen options", generatorOptions);
		} else {
			dialogs.displayInfo("World MapGen options", "There are currently no mapgen options in use");
		}		
	}

	/**
	 * This is more useful for Minetest than Minecraft, as Minetest has biomes
	 * determined by heat and humidy, which Minecraft hasn't had since early betas
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public void displayBiomeProfileVoronoi() {
		if (gameEngineDetails.getType() != GameEngineType.MINECRAFT) {
			// Passing a null IClimateHistogram because the default one is currently the 
			// only one implemented anyway.
			// It might be worth passing this value once Amidstest is reading game profiles,
			// as then it will at least know if the current world DOESN'T use the default climate settings.
			dialogs.displayVoronoiDiagram(biomeAuthority.getBiomeProfileSelection(), mapgenRelay, null);
		} else {
			dialogs.displayInfo("Biome profile Voronoi diagram", "Minecraft biomes are not determined by heat and humidity.");
		}		
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	public void about() {
		dialogs.displayInfo(
				"About",
				"Amidst - Advanced Minecraft Interfacing and Data/Structure Tracking\n"
						+ (FeatureToggles.MINETEST_SUPPORT ? "Amidst for Minetest - The Amidst project adapted to support Minetest\n\n" : "\n")
						+ amidstVersion.createVersionString() + "\n"
						+ "Project page: https://github.com/Treer/amidstest\n"
						+ "Forum thread: https://forum.minetest.net/viewtopic.php?t=19869");
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
			AmidstLogger.warn(e);
			dialogs.displayError(e);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private File appendPNGFileExtensionIfNecessary(File file) {
		String filename = file.getAbsolutePath();
		if (!FileExtensionChecker.hasFileExtension(filename, "png")) {
			filename += ".png";
		}
		return new File(filename);
	}
}
