package amidst.gui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import MoF.FinderWindow;
import MoF.SaveLoader;
import amidst.Util;

public class MenuActionsHelper {
	private FinderWindow window;

	public MenuActionsHelper(FinderWindow window) {
		this.window = window;
	}

	public String showSeedPrompt(String title) {
		final String blankText = "A random seed will be generated if left blank.";
		final String leadingSpaceText = "Warning: There is a space at the start!";
		final String trailingSpaceText = "Warning: There is a space at the end!";

		final JTextField inputText = new JTextField();

		inputText.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				inputText.requestFocus();
			}

			@Override
			public void ancestorMoved(AncestorEvent arg0) {
				inputText.requestFocus();
			}

			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
				inputText.requestFocus();
			}
		});

		final JLabel inputInformation = new JLabel(blankText);
		inputInformation.setForeground(Color.red);
		inputInformation.setFont(new Font("arial", Font.BOLD, 10));
		inputText.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			public void update() {
				String text = inputText.getText();
				if (text.equals("")) {
					inputInformation.setText(blankText);
					inputInformation.setForeground(Color.red);
				} else if (text.startsWith(" ")) {
					inputInformation.setText(leadingSpaceText);
					inputInformation.setForeground(Color.red);
				} else if (text.endsWith(" ")) {
					inputInformation.setText(trailingSpaceText);
					inputInformation.setForeground(Color.red);
				} else {
					try {
						Long.parseLong(text);
						inputInformation.setText("Seed is valid.");
						inputInformation.setForeground(Color.gray);
					} catch (NumberFormatException e) {
						inputInformation.setText("This seed's value is "
								+ text.hashCode() + ".");
						inputInformation.setForeground(Color.black);
					}
				}
			}
		});

		final JComponent[] inputs = new JComponent[] {
				new JLabel("Enter your seed: "), inputInformation, inputText };
		int result = JOptionPane.showConfirmDialog(window.getFrame(), inputs,
				title, JOptionPane.OK_CANCEL_OPTION);
		return (result == 0) ? inputText.getText() : null;
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
		return fileChooser.showOpenDialog(window.getFrame());
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
		return (T) JOptionPane.showInputDialog(window.getFrame(), message,
				title, JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
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
			window.getProject().moveMapTo(p.x, p.y);
	}
}
