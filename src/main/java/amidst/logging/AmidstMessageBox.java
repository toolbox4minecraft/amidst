package amidst.logging;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public enum AmidstMessageBox {
	;

	public static void displayError(String title, Throwable e) {
		displayMessageBox(title, MessageFormatter.format(e), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayError(String title, Throwable e, String message) {
		displayMessageBox(title, MessageFormatter.format(e, message), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayError(String title, Throwable e, String message, Object part1, Object[] parts) {
		displayMessageBox(title, MessageFormatter.format(e, message, part1, parts), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayError(String title, String message) {
		displayMessageBox(title, MessageFormatter.format(message), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayError(String title, String message, Object part1, Object[] parts) {
		displayMessageBox(title, MessageFormatter.format(message, part1, parts), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayInfo(String title, String message) {
		displayMessageBox(title, MessageFormatter.format(message), JOptionPane.INFORMATION_MESSAGE);
	}

	public static void displayInfo(String title, String message, Object part1, Object[] parts) {
		displayMessageBox(title, MessageFormatter.format(message, part1, parts), JOptionPane.INFORMATION_MESSAGE);
	}

	public static void displayError(Component parent, String title, Throwable e) {
		displayMessageBox(parent, title, MessageFormatter.format(e), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayError(Component parent, String title, Throwable e, String message) {
		displayMessageBox(parent, title, MessageFormatter.format(e, message), JOptionPane.ERROR_MESSAGE);
	}
	
	public static void displayWarning(String title, String message) {
		displayMessageBox(title, MessageFormatter.format(message), JOptionPane.WARNING_MESSAGE);
	}

	public static void displayWarning(String title, String message, Object part1, Object[] parts) {
		displayMessageBox(title, MessageFormatter.format(message, part1, parts), JOptionPane.WARNING_MESSAGE);
	}
	
	public static void displayWarning(Component parent, String title, String message) {
		displayMessageBox(parent, title, MessageFormatter.format(message), JOptionPane.WARNING_MESSAGE);
	}

	public static void displayWarning(Component parent, String title, String message, Object part1, Object[] parts) {
		displayMessageBox(
				parent,
				title,
				MessageFormatter.format(message, part1, parts),
				JOptionPane.WARNING_MESSAGE);
	}

	public static void displayError(
			Component parent,
			String title,
			Throwable e,
			String message,
			String part1,
			String[] parts) {
		displayMessageBox(parent, title, MessageFormatter.format(e, message, part1, parts), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayError(Component parent, String title, String message) {
		displayMessageBox(parent, title, MessageFormatter.format(message), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayError(Component parent, String title, String message, Object part1, Object[] parts) {
		displayMessageBox(parent, title, MessageFormatter.format(message, part1, parts), JOptionPane.ERROR_MESSAGE);
	}

	public static void displayInfo(Component parent, String title, String message) {
		displayMessageBox(parent, title, MessageFormatter.format(message), JOptionPane.INFORMATION_MESSAGE);
	}

	public static void displayInfo(Component parent, String title, String message, Object part1, Object[] parts) {
		displayMessageBox(
				parent,
				title,
				MessageFormatter.format(message, part1, parts),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private static void displayMessageBox(String title, String message, int type) {
		displayMessageBox(null, title, message, type);
	}

	private static void displayMessageBox(Component parent, String title, String message, int type) {
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parent, message, title, type));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public static boolean askToConfirmYesNo(String title, String message) {
		return askToConfirmYesNo(null, title, message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public static boolean askToConfirmYesNo(Component parent, String title, String message) {
		return JOptionPane
				.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
}
