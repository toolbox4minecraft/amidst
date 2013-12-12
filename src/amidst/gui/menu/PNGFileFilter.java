package amidst.gui.menu;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class PNGFileFilter extends FileFilter {
	@Override
	public boolean accept(File file) {
		if (file.isDirectory())
			return true;
		String[] st = file.getName().split("/.");
		return st[st.length - 1].equalsIgnoreCase("png");
	}
	
	@Override
	public String getDescription() {
		return "Portable Network Graphic (*.PNG)";
	}
}
