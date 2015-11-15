package amidst.gui.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import amidst.Application;
import amidst.Options;
import amidst.gui.MapWindow;
import amidst.logging.Log;
import amidst.map.MapObjectStronghold;
import amidst.map.layers.StrongholdLayer;
import amidst.minecraft.world.FileWorld.Player;
import amidst.minecraft.world.WorldType;
import amidst.minecraft.world.Worlds;

public class MenuActions {
	private static final String ABOUT_MESSAGE = "Advanced Minecraft Interfacing and Data/Structure Tracking (AMIDST)\n"
			+ "By Skidoodle (amidst.project@gmail.com)";

	private Application application;
	private MapWindow mapWindow;

	public MenuActions(Application application, MapWindow mapWindow) {
		this.application = application;
		this.mapWindow = mapWindow;
	}

	public void savePlayerLocations() {
		if (application.isFileWorld()) {
			for (Player player : application.getWorldAsFileWorld().getPlayers()) {
				player.saveLocation();
			}
		}
	}

	public void exit() {
		application.exitGracefully();
	}

	public void newFromSeed() {
		String seed = mapWindow.askForSeed();
		if (seed != null) {
			if (seed.isEmpty()) {
				newFromRandom();
			} else {
				WorldType worldType = mapWindow.askForWorldType();
				if (worldType != null) {
					application.setWorld(Worlds.fromSeed(seed, worldType));
				}
			}
		}
	}

	public void newFromRandom() {
		WorldType worldType = mapWindow.askForWorldType();
		if (worldType != null) {
			application.setWorld(Worlds.random(worldType));
		}
	}

	public void newFromFileOrFolder() {
		File worldFile = mapWindow.askForMinecraftMapFile();
		if (worldFile != null) {
			try {
				application.setWorld(Worlds.fromFile(worldFile));
			} catch (Exception e) {
				mapWindow.displayException(e);
			}
		}
	}

	public void findStronghold() {
		MapObjectStronghold selection = mapWindow
				.askForOptions("Go to", "Select Stronghold:",
						StrongholdLayer.instance.getStrongholds());
		if (selection != null) {
			mapWindow.moveMapTo(selection.x, selection.y);
		}
	}

	public void gotoCoordinate() {
		String s = JOptionPane.showInputDialog(null,
				"Enter coordinates: (Ex. 123,456)", "Go To",
				JOptionPane.QUESTION_MESSAGE);
		if (s != null) {
			String[] c = s.replaceAll(" ", "").split(",");
			try {
				long x = Long.parseLong(c[0]);
				long y = Long.parseLong(c[1]);
				mapWindow.moveMapTo(x, y);
			} catch (NumberFormatException e1) {
				Log.w("Invalid location entered, ignoring.");
				e1.printStackTrace();
			}
		}
	}

	public void gotoPlayer() {
		if (application.isFileWorld()) {
			List<Player> playerList = application.getWorldAsFileWorld()
					.getPlayers();
			Player[] playerArray = playerList.toArray(new Player[playerList
					.size()]);
			Player selection = mapWindow.askForOptions("Go to",
					"Select player:", playerArray);
			if (selection != null) {
				mapWindow.moveMapTo(selection.getX(), selection.getZ());
			}
		} else {
			mapWindow.displayMessage("There are no players on the map");
		}
	}

	public void capture() {
		File file = mapWindow.askForScreenshotSaveFile();
		if (file != null) {
			mapWindow.capture(file);
		}
	}

	public void copySeedToClipboard() {
		StringSelection stringSelection = new StringSelection(
				Options.instance.seed + "");
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, new ClipboardOwner() {
			@Override
			public void lostOwnership(Clipboard arg0, Transferable arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void checkForUpdates() {
		application.checkForUpdates();
	}

	public void viewLicense() {
		application.displayLicenseWindow();
	}

	public void about() {
		mapWindow.displayMessage(ABOUT_MESSAGE);
	}
}
