package amidst.gui.main;

import java.nio.file.Path;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import amidst.AmidstSettings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.player.WorldPlayerType;

@NotThreadSafe
public class MainWindowDialogs {
	private final AmidstSettings settings;
	private final RunningLauncherProfile runningLauncherProfile;
	private final JFrame frame;

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindowDialogs(AmidstSettings settings, RunningLauncherProfile runningLauncherProfile, JFrame frame) {
		this.settings = settings;
		this.runningLauncherProfile = runningLauncherProfile;
		this.frame = frame;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldPlayerType askForWorldPlayerType() {
		return askForOptions(
				"Loading World",
				"This world contains Multiplayer and Singleplayer data. What do you want to load?\n"
						+ "If you do not know what to do, just choose Singleplayer.",
				WorldPlayerType.getSelectable());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldSeed askForSeed() {
		return new SeedPrompt(frame).askForSeed();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Path askForSaveGame() {
		return showOpenDialogAndGetSelectedFileOrNull(createSaveGameFileChooser());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFileChooser createSaveGameFileChooser() {
		JFileChooser result = new JFileChooser(runningLauncherProfile.getLauncherProfile().getSaves().toFile());
		result.setFileFilter(new LevelFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		result.setFileHidingEnabled(false);
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Path showOpenDialogAndGetSelectedFileOrNull(JFileChooser fileChooser) {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile().toPath();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Path askForPNGSaveFile(String suggestedFilename) {
		return showSaveDialogAndGetSelectedFileOrNull(createPNGSaveFileChooser(suggestedFilename));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFileChooser createPNGSaveFileChooser(String suggestedFilename) {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new PNGFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setSelectedFile(new java.io.File(suggestedFilename));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Path showSaveDialogAndGetSelectedFileOrNull(JFileChooser fileChooser) {
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile().toPath();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayInfo(String title, String message) {
		AmidstMessageBox.displayInfo(frame, title, message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayError(String message) {
		AmidstMessageBox.displayError(frame, "Error", message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayError(Exception e) {
		AmidstMessageBox.displayError(frame, "Error", e);
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	public void displayWarning(String message) {
		AmidstMessageBox.displayWarning(frame, "Warning", message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean askToConfirmSaveGameManipulation() {
		return askToConfirmYesNo(
				"Save Game Manipulation",
				"WARNING: You are about to change the contents of the save game directory. There is a chance that it gets corrupted.\n"
						+ "We try to minimize the risk by creating a backup of the changed file, before it is changed.\n"
						+ "If the backup fails, we will not write the changes.\n"
						+ "You can find the backup files in the directory 'amidst/backup', which is placed in the save game directory.\n"
						+ "Especially, make sure to not have the save game loaded in Minecraft during this process.\n\n"
						+ "Do you want to proceed?");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean askToConfirmYesNo(String title, String message) {
		return AmidstMessageBox.askToConfirmYesNo(frame, title, message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldType askForWorldType() {
		String worldTypeSetting = settings.worldType.get();
		if (worldTypeSetting.equals(WorldType.PROMPT_EACH_TIME)) {
			return askForOptions("World Type", "Enter world type\n", WorldType.getSelectable());
		} else {
			return WorldType.from(worldTypeSetting);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@SuppressWarnings("unchecked")
	public <T> T askForOptions(String title, String message, List<T> choices) {
		Object[] choicesArray = choices.toArray();
		return (T) JOptionPane
				.showInputDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE, null, choicesArray, choicesArray[0]);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public String askForCoordinates() {
		return askForString("Go To", "Enter coordinates: (Ex. 123,456)");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public String askForPlayerHeight(long currentHeight) {
		Object input = JOptionPane.showInputDialog(
				frame,
				"Enter new player height:",
				"Move Player",
				JOptionPane.QUESTION_MESSAGE,
				null,
				null,
				currentHeight);
		if (input != null) {
			return input.toString();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String askForString(String title, String message) {
		return JOptionPane.showInputDialog(frame, message, title, JOptionPane.QUESTION_MESSAGE);
	}
	
}
