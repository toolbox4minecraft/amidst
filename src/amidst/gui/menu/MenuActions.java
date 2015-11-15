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

	public MenuActions(Application application) {
		this.application = application;
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
		String seed = application.getMapWindow().askForSeed();
		if (seed != null) {
			if (seed.isEmpty()) {
				newFromRandom();
			} else {
				WorldType worldType = application.getMapWindow()
						.askForWorldType();
				if (worldType != null) {
					application.setWorld(Worlds.fromSeed(seed, worldType));
				}
			}
		}
	}

	public void newFromRandom() {
		WorldType worldType = application.getMapWindow().askForWorldType();
		if (worldType != null) {
			application.setWorld(Worlds.random(worldType));
		}
	}

	public void newFromFileOrFolder() {
		File worldFile = application.getMapWindow().askForMinecraftMapFile();
		if (worldFile != null) {
			try {
				application.setWorld(Worlds.fromFile(worldFile));
			} catch (Exception e) {
				application.getMapWindow().displayException(e);
			}
		}
	}

	public void findStronghold() {
		MapObjectStronghold selection = application.getMapWindow()
				.askForOptions("Go to", "Select Stronghold:",
						StrongholdLayer.instance.getStrongholds());
		if (selection != null) {
			application.getMapWindow().moveMapTo(selection.x, selection.y);
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
				application.getMapWindow().moveMapTo(x, y);
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
			Player selection = application.getMapWindow().askForOptions(
					"Go to", "Select player:", playerArray);
			if (selection != null) {
				application.getMapWindow().moveMapTo(selection.getX(),
						selection.getZ());
			}
		} else {
			application.getMapWindow().displayMessage(
					"There are no players on the map");
		}
	}

	public void capture() {
		File file = application.getMapWindow().askForScreenshotSaveFile();
		if (file != null) {
			String filename = file.toString();
			if (!filename.toLowerCase().endsWith(".png")) {
				filename += ".png";
			}
			application.getProject().getMapViewer()
					.saveToFile(new File(filename));
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
		application.getMapWindow().displayMessage(ABOUT_MESSAGE);
	}
}
