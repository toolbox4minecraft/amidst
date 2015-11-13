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

import MoF.FinderWindow;
import MoF.Project;
import MoF.SaveLoader;
import MoF.UpdateManager;
import amidst.Options;
import amidst.Util;
import amidst.gui.LicenseWindow;
import amidst.logging.Log;
import amidst.map.MapObjectPlayer;
import amidst.map.layers.StrongholdLayer;

public class MenuActions {
	private MenuActionsHelper helper;
	private FinderWindow window;

	public MenuActions(FinderWindow window) {
		this.window = window;
		this.helper = new MenuActionsHelper(window);
	}

	public void savePlayerLocations() {
		if (window.getProject().isSaveLoaded()) {
			for (MapObjectPlayer player : window.getProject().getSaveLoader()
					.getPlayers()) {
				if (player.needSave) {
					window.getProject()
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
				window.clearProject();
				window.setProject(new Project(seed, worldType.getValue()));
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
			window.clearProject();
			window.setProject(new Project(seed, worldType.getValue()));
		}
	}

	public void newFromFileOrFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(SaveLoader.getFilter());
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		File savesDir = null;
		if (Util.profileDirectory != null)
			savesDir = new File(Util.profileDirectory, "saves");
		else
			savesDir = new File(Util.minecraftDirectory, "saves");
		// if (!savesDir.mkdirs()) {
		// Log.w("Unable to create save directory!");
		// return;
		// }
		fc.setCurrentDirectory(savesDir);
		fc.setFileHidingEnabled(false);
		if (fc.showOpenDialog(window.getFrame()) == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();

			SaveLoader s = null;
			if (f.isDirectory())
				s = new SaveLoader(new File(f.getAbsoluteFile() + "/level.dat"));
			else
				s = new SaveLoader(f);
			window.clearProject();
			window.setProject(new Project(s));
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
				window.getProject().moveMapTo(x, y);
			} catch (NumberFormatException e1) {
				Log.w("Invalid location entered, ignoring.");
				e1.printStackTrace();
			}
		}
	}

	public void gotoPlayer() {
		if (window.getProject().isSaveLoaded()) {
			List<MapObjectPlayer> playerList = window.getProject()
					.getSaveLoader().getPlayers();
			MapObjectPlayer[] players = playerList
					.toArray(new MapObjectPlayer[playerList.size()]);
			helper.goToChosenPoint(players, "Player");
			MapObjectPlayer p = helper.choose("Go to", "Select player:",
					players);
			if (p != null)
				window.getProject().moveMapTo(p.globalX, p.globalY);
		}
	}

	public void capture() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new PNGFileFilter());
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showSaveDialog(window.getFrame());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String s = fc.getSelectedFile().toString();
			if (!s.toLowerCase().endsWith(".png"))
				s += ".png";
			window.getProject().getMapViewer().saveToFile(new File(s));
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
		new UpdateManager(window.getFrame()).start();
	}

	public void viewLicense() {
		new LicenseWindow();
	}

	public void about() {
		JOptionPane.showMessageDialog(window.getFrame(),
				"Advanced Minecraft Interfacing and Data/Structure Tracking (AMIDST)\n"
						+ "By Skidoodle (amidst.project@gmail.com)");
	}
}
