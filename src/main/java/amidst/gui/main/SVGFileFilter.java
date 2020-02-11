package amidst.gui.main;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import amidst.documentation.NotThreadSafe;
import amidst.util.FileExtensionChecker;

@NotThreadSafe
public class SVGFileFilter extends FileFilter {
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			return FileExtensionChecker.hasFileExtension(file.getName(), "svg");
		}
	}

	@Override
	public String getDescription() {
		return "Scalable Vector Graphics (*.SVG)";
	}
}
