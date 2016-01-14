package amidst.mojangapi.file.directory;

import java.io.File;

import amidst.documentation.Immutable;

@Immutable
public class ProfileDirectory {
	private final File root;
	private final File saves;

	public ProfileDirectory(File root) {
		this.root = root;
		this.saves = new File(root, "saves");
	}

	public boolean isValid() {
		return root.isDirectory();
	}

	public File getRoot() {
		return root;
	}

	public File getSaves() {
		return saves;
	}
}
