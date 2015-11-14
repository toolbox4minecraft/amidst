package amidst.gui.menu;

import java.awt.Point;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import MoF.SaveLoader;
import amidst.Application;
import amidst.Util;

public class MenuActionsHelper {
	private Application application;

	public MenuActionsHelper(Application application) {
		this.application = application;
	}

	public File getSavesDirectory() {
		if (Util.profileDirectory != null) {
			return new File(Util.profileDirectory, "saves");
		} else {
			return new File(Util.minecraftDirectory, "saves");
		}
	}

	public SaveLoader getSaveLoader(File file) {
		if (file.isDirectory()) {
			return new SaveLoader(new File(file.getAbsoluteFile()
					+ "/level.dat"));
		} else {
			return new SaveLoader(file);
		}
	}

	public int showFileChooser(JFileChooser fileChooser) {
		return fileChooser
				.showOpenDialog(application.getMapWindow().getFrame());
	}

	public JFileChooser createMinecraftMapFileChooser() {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(SaveLoader.getFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		result.setCurrentDirectory(getSavesDirectory());
		result.setFileHidingEnabled(false);
		return result;
	}

	/**
	 * Allows the user to choose one of several things.
	 * 
	 * Convenience wrapper around JOptionPane.showInputDialog
	 */
	public <T> T choose(String title, String message, T[] choices) {
		return (T) JOptionPane.showInputDialog(application.getMapWindow()
				.getFrame(), message, title, JOptionPane.PLAIN_MESSAGE, null,
				choices, choices[0]);
	}

	/**
	 * Lets the user decide one of the given points and go to it
	 * 
	 * @param points
	 *            Given points to choose from
	 * @param name
	 *            name displayed in the choice
	 */
	public <T extends Point> void goToChosenPoint(T[] points, String name) {
		T p = choose("Go to", "Select " + name + ":", points);
		if (p != null)
			application.getProject().moveMapTo(p.x, p.y);
	}
}
