package amidst.gui.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import MoF.Project;
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
			String worldTypePreference = Options.instance.worldType.get();
			WorldType worldType = null;
			if (worldTypePreference.equals("Prompt each time")) {
				worldType = application.getMapWindow().askForOptions(
						"New Project", "Enter world type\n",
						WorldType.getSelectable());
			} else {
				worldType = WorldType.from(worldTypePreference);
			}

			if (seed.equals(""))
				seed = "" + (new Random()).nextLong();
			if (worldType != null) {
				application.setProject(new Project(application, seed, worldType
						.getValue()));
			}
		}
	}

	public void newFromRandom() {
		// Create the JOptionPane.
		Random random = new Random();
		long seed = random.nextLong();
		String worldTypePreference = Options.instance.worldType.get();
		WorldType worldType = null;
		if (worldTypePreference.equals("Prompt each time")) {
			worldType = application.getMapWindow().askForOptions("New Project",
					"Enter world type\n", WorldType.getSelectable());
		} else {
			worldType = WorldType.from(worldTypePreference);
		}

		// If a string was returned, say so.
		if (worldType != null) {
			application.setProject(new Project(application, seed, worldType
					.getValue()));
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
