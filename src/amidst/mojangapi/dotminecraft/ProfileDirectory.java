package amidst.mojangapi.dotminecraft;

import java.io.File;

public class ProfileDirectory {
	private final File root;
	private final File saves;

	public ProfileDirectory(File root) {
		this.root = root;
		this.saves = new File(root, "saves");
	}

	public boolean isValid() {
		return root.isDirectory() && saves.isDirectory();
	}

	public File getRoot() {
		return root;
	}

	public File getSaves() {
		return saves;
	}
}
