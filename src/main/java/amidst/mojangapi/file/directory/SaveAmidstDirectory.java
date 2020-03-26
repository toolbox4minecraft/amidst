package amidst.mojangapi.file.directory;

import java.nio.file.Path;

public class SaveAmidstDirectory {
	private final Path root;
	private final SaveAmidstBackupDirectory backup;

	public SaveAmidstDirectory(Path root) {
		this.root = root;
		this.backup = new SaveAmidstBackupDirectory(root.resolve("backup"));
	}

	public Path getRoot() {
		return root;
	}

	public SaveAmidstBackupDirectory getBackup() {
		return backup;
	}
}
