package amidst.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SeedPrompt {
	private static final String TITLE = "Enter Seed";
	private static final String BLANK_TEXT = "A random seed will be generated if left blank.";
	private static final String LEADING_SPACE_TEXT = "Warning: There is a space at the start!";
	private static final String TRAILING_SPACE_TEXT = "Warning: There is a space at the end!";

	private JFrame frame;
	private JLabel label;
	private JTextField textField;
	private JComponent[] inputs;

	public SeedPrompt(JFrame frame) {
		this.frame = frame;
		init();
	}

	private void init() {
		createLabel();
		createTextField();
		createInputs();
	}

	private void createLabel() {
		label = new JLabel(BLANK_TEXT);
		label.setForeground(Color.red);
		label.setFont(new Font("arial", Font.BOLD, 10));
	}

	private void createTextField() {
		textField = new JTextField();
		textField.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				textField.requestFocus();
			}

			@Override
			public void ancestorMoved(AncestorEvent arg0) {
				textField.requestFocus();
			}

			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
				textField.requestFocus();
			}
		});
		textField.getDocument().addDocumentListener(new DocumentListener() {
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
	}

	private void createInputs() {
		inputs = new JComponent[] { createSimpleLabel(), label, textField };
	}

	private JLabel createSimpleLabel() {
		return new JLabel("Enter your seed: ");
	}

	private void update() {
		String text = textField.getText();
		if (text.equals("")) {
			label.setText(BLANK_TEXT);
			label.setForeground(Color.red);
		} else if (text.startsWith(" ")) {
			label.setText(LEADING_SPACE_TEXT);
			label.setForeground(Color.red);
		} else if (text.endsWith(" ")) {
			label.setText(TRAILING_SPACE_TEXT);
			label.setForeground(Color.red);
		} else if (isLong(text)) {
			label.setText("Seed is valid.");
			label.setForeground(Color.gray);
		} else {
			label.setText("This seed's value is " + text.hashCode() + ".");
			label.setForeground(Color.black);
		}
	}

	private boolean isLong(String text) {
		try {
			Long.parseLong(text);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public String askForSeed() {
		textField.setText("");
		textField.requestFocus();
		if (JOptionPane.showConfirmDialog(frame, inputs, TITLE,
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			return textField.getText();
		} else {
			return null;
		}
	}
}
