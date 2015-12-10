package amidst.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldSeed.WorldSeedType;

public class SeedPrompt {
	private static final String TITLE = "Enter Seed";
	private static final String LEADING_SPACE_TEXT = "WARNING: There is a space at the start!";
	private static final String TRAILING_SPACE_TEXT = "WARNING: There is a space at the end!";

	private final JFrame frame;
	private final JTextField textField;
	private final JLabel seedLabel;
	private final JLabel warningLabel;
	private final JComponent[] inputs;
	private WorldSeed seed;

	public SeedPrompt(JFrame frame) {
		this.frame = frame;
		this.textField = createTextField();
		this.seedLabel = createSeedLabel();
		this.warningLabel = createWarningLabel();
		this.inputs = createInputs();
	}

	private JTextField createTextField() {
		JTextField result = new JTextField();
		result.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent e) {
				grabFocus();
			}

			@Override
			public void ancestorMoved(AncestorEvent e) {
				grabFocus();
			}

			@Override
			public void ancestorRemoved(AncestorEvent e) {
				grabFocus();
			}
		});
		result.getDocument().addDocumentListener(new DocumentListener() {
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
		});
		return result;
	}

	private JLabel createSeedLabel() {
		JLabel result = new JLabel();
		result.setFont(new Font("arial", Font.BOLD, 12));
		return result;
	}

	private JLabel createWarningLabel() {
		JLabel result = new JLabel();
		result.setFont(new Font("arial", Font.BOLD, 12));
		result.setPreferredSize(new Dimension(400, 30));
		result.setForeground(Color.RED);
		return result;
	}

	private JComponent[] createInputs() {
		return new JComponent[] { new JLabel("Enter your seed:"), textField,
				seedLabel, warningLabel };
	}

	private void grabFocus() {
		// The call with invokeLater seems to help resolve an issue on Linux.
		// Without it, the textField often does not get the focus.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textField.requestFocus();
			}
		});
	}

	private void update() {
		seed = WorldSeed.fromUserInput(textField.getText());
		if (WorldSeedType.TEXT == seed.getType()
				&& seed.getText().startsWith(" ")) {
			warningLabel.setText(LEADING_SPACE_TEXT);
		} else if (WorldSeedType.TEXT == seed.getType()
				&& seed.getText().endsWith(" ")) {
			warningLabel.setText(TRAILING_SPACE_TEXT);
		} else {
			warningLabel.setText("");
		}
		seedLabel.setText(seed.getLabel());
		seedLabel.revalidate();
	}

	public WorldSeed askForSeed() {
		update();
		grabFocus();
		if (JOptionPane.showConfirmDialog(frame, inputs, TITLE,
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			return seed;
		} else {
			return null;
		}
	}
}
