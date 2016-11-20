package amidst.gui.main;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldSeed.WorldSeedType;
import net.miginfocom.swing.MigLayout;

@NotThreadSafe
public class SeedPrompt {
	private static final String TITLE = "Enter Seed";
	private static final String STARTS_WITH_SPACE_TEXT = "WARNING: There is a space at the start!";
	private static final String ENDS_WITH_SPACE_TEXT = "WARNING: There is a space at the end!";

	private final JFrame frame;
	private final JTextField textField;
	private final JLabel seedLabel;
	private final JLabel warningLabel;
	private final JPanel panel;
	private WorldSeed seed;

	public SeedPrompt(JFrame frame) {
		this.frame = frame;
		this.textField = createTextField();
		this.seedLabel = createSeedLabel();
		this.warningLabel = createWarningLabel();
		this.panel = createPanel();
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
		result.setForeground(Color.RED);
		return result;
	}

	private JPanel createPanel() {
		JPanel result = new JPanel(new MigLayout());
		result.add(new JLabel("Enter your seed:"), "w 400!, wrap");
		result.add(textField, "w 400!, wrap");
		result.add(seedLabel, "w 400!, wrap");
		result.add(warningLabel, "w 400!, h 30!");
		return result;
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
		if (WorldSeedType.TEXT == seed.getType() && seed.getText().startsWith(" ")) {
			warningLabel.setText(STARTS_WITH_SPACE_TEXT);
		} else if (WorldSeedType.TEXT == seed.getType() && seed.getText().endsWith(" ")) {
			warningLabel.setText(ENDS_WITH_SPACE_TEXT);
		} else {
			warningLabel.setText("");
		}
		seedLabel.setText(seed.getLabel());
		seedLabel.revalidate();
	}

	public WorldSeed askForSeed() {
		update();
		grabFocus();
		if (JOptionPane.showConfirmDialog(frame, panel, TITLE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			return seed;
		} else {
			return null;
		}
	}
}
