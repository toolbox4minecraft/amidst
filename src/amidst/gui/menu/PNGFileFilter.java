package amidst.gui.menu;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PNGFileFilter extends FileFilter {
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			return getFileExtension(file.getName()).equalsIgnoreCase("png");
		}
	}

	private String getFileExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i + 1);
		} else {
			return "";
		}
	}

	@Override
	public String getDescription() {
		return "Portable Network Graphic (*.PNG)";
	}
}
