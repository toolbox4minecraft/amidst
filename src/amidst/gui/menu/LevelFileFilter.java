package amidst.gui.menu;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class LevelFileFilter extends FileFilter {
	private static final String LEVEL_DAT = "level.dat";

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			return file.getName().equalsIgnoreCase(LEVEL_DAT);
		}
	}

	@Override
	public String getDescription() {
		return "Minecraft Data File (level.dat)";
	}
}
