package amidst;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Util {
	/** Shows an error message for an exception
	 * @param e the exception for which the stachtrace is to be shown
	 */
	public static void showError(Exception e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		String trace = baos.toString();
		
		e.printStackTrace();
		
		JOptionPane.showMessageDialog(
			null,
			trace,
			e.toString(),
			JOptionPane.ERROR_MESSAGE);
	}
	
	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {}
	}
	
//	public static void main(String[] args) {
//		try {
//			int infinity = 1 / 0;
//		} catch (Exception e) {
//			showError(e);
//		}
//	}
}
