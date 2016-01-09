package amidst.mojangapi.file.directory;

import java.io.File;

public class SaveAmidstDirectory {
	private final File root;
	private final SaveAmidstBackupDirectory backup;

	public SaveAmidstDirectory(File root) {
		this.root = root;
		this.backup = new SaveAmidstBackupDirectory(new File(root, "backup"));
	}

	public File getRoot() {
		return root;
	}

	public SaveAmidstBackupDirectory getBackup() {
		return backup;
	}
}
