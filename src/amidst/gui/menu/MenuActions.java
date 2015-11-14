package amidst.gui.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import MoF.Project;
import MoF.SaveLoader;
import MoF.UpdateManager;
import amidst.Application;
import amidst.Options;
import amidst.gui.LicenseWindow;
import amidst.logging.Log;
import amidst.map.MapObjectPlayer;
import amidst.map.layers.StrongholdLayer;

public class MenuActions {
	private MenuActionsHelper helper;
	private Application application;

	public MenuActions(Application application) {
		this.application = application;
		this.helper = new MenuActionsHelper(application);
	}

	public void savePlayerLocations() {
		if (application.getProject().isSaveLoaded()) {
			for (MapObjectPlayer player : application.getProject()
					.getSaveLoader().getPlayers()) {
				if (player.needSave) {
					application
							.getProject()
							.getSaveLoader()
							.movePlayer(player.getName(), player.globalX,
									player.globalY);
					player.needSave = false;
				}
			}
		}
	}

	public void exit() {
		System.exit(0);
	}

	public void newFromSeed() {
		String seed = helper.showSeedPrompt("New Project");
		if (seed != null) {
			String worldTypePreference = Options.instance.worldType.get();
			SaveLoader.Type worldType = null;
			if (worldTypePreference.equals("Prompt each time")) {
				worldType = helper.choose("New Project", "Enter world type\n",
						SaveLoader.selectableTypes);
			} else {
				worldType = SaveLoader.Type.fromMixedCase(worldTypePreference);
			}

			if (seed.equals(""))
				seed = "" + (new Random()).nextLong();
			if (worldType != null) {
				application.setProject(new Project(seed, worldType.getValue()));
			}
		}
	}

	public void newFromRandom() {
		// Create the JOptionPane.
		Random random = new Random();
		long seed = random.nextLong();
		String worldTypePreference = Options.instance.worldType.get();
		SaveLoader.Type worldType = null;
		if (worldTypePreference.equals("Prompt each time")) {
			worldType = helper.choose("New Project", "Enter world type\n",
					SaveLoader.selectableTypes);
		} else {
			worldType = SaveLoader.Type.fromMixedCase(worldTypePreference);
		}

		// If a string was returned, say so.
		if (worldType != null) {
			application.setProject(new Project(seed, worldType.getValue()));
		}
	}

	public void newFromFileOrFolder() {
		JFileChooser fileChooser = helper.createMinecraftMapFileChooser();
		if (helper.showFileChooser(fileChooser) == JFileChooser.APPROVE_OPTION) {
			application.setProject(new Project(helper.getSaveLoader(fileChooser
					.getSelectedFile())));
		}
	}

	public void findStronghold() {
		helper.goToChosenPoint(StrongholdLayer.instance.getStrongholds(),
				"Stronghold");
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
				application.getProject().moveMapTo(x, y);
			} catch (NumberFormatException e1) {
				Log.w("Invalid location entered, ignoring.");
				e1.printStackTrace();
			}
		}
	}

	public void gotoPlayer() {
		if (application.getProject().isSaveLoaded()) {
			List<MapObjectPlayer> playerList = application.getProject()
					.getSaveLoader().getPlayers();
			MapObjectPlayer[] players = playerList
					.toArray(new MapObjectPlayer[playerList.size()]);
			helper.goToChosenPoint(players, "Player");
			MapObjectPlayer p = helper.choose("Go to", "Select player:",
					players);
			if (p != null)
				application.getProject().moveMapTo(p.globalX, p.globalY);
		}
	}

	public void capture() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new PNGFileFilter());
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc
				.showSaveDialog(application.getMapWindow().getFrame());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String s = fc.getSelectedFile().toString();
			if (!s.toLowerCase().endsWith(".png")) {
				s += ".png";
			}
			application.getProject().getMapViewer().saveToFile(new File(s));
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
		new UpdateManager(application.getMapWindow().getFrame()).start();
	}

	public void viewLicense() {
		new LicenseWindow();
	}

	public void about() {
		JOptionPane.showMessageDialog(application.getMapWindow().getFrame(),
				"Advanced Minecraft Interfacing and Data/Structure Tracking (AMIDST)\n"
						+ "By Skidoodle (amidst.project@gmail.com)");
	}
}
