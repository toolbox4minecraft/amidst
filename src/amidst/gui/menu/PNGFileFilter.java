package amidst.gui.menu;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PNGFileFilter extends FileFilter {
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			return file.getName().toLowerCase().endsWith(".png");
		}
	}

	@Override
	public String getDescription() {
		return "Portable Network Graphic (*.PNG)";
	}
}
