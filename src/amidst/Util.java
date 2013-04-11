package amidst;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
	
	public static final File minecraftDirectory;
	static {
		File mcDir = null;
		File homeDirectory = new File(System.getProperty("user.home", "."));
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("win")) {
			File appData = new File(System.getenv("APPDATA"));
			if (appData.isDirectory())
				mcDir = new File(appData, ".minecraft");
		} else if (os.contains("mac"))
			mcDir = new File(homeDirectory, "Library/Application Support/minecraft");
		
		minecraftDirectory = (mcDir != null) ? mcDir : new File(homeDirectory, ".minecraft");
	}
	
	// Added for scripting support. (javascript has issues retaining the array types)
	public static int[] arrayToColors(int[] src, int[] dst, int[] colors, int length) {
		for (int i = 0; i < length; i++) {
			dst[i] = colors[src[i]];
		}
		return dst;
	}
	
	public static int makeColor(int r, int g, int b) {
		int color = 0xFF000000;
		color |= 0xFF0000 & (r << 16);
		color |= 0xFF00 & (g << 8);
		color |= 0xFF & b;
		return color;
	}
//	public static void main(String[] args) {
//		try {
//			int infinity = 1 / 0;
//		} catch (Exception e) {
//			showError(e);
//		}
//	}
}
