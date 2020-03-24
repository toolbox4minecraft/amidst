package amidst.mojangapi.file.directory;

import java.nio.file.Files;
import java.nio.file.Path;

import amidst.documentation.Immutable;

@Immutable
public class ProfileDirectory {
	private final Path root;
	private final Path saves;

	public ProfileDirectory(Path root) {
		this.root = root;
		this.saves = root.resolve("saves");
	}

	public boolean isValid() {
		return Files.isDirectory(root);
	}

	public Path getRoot() {
		return root;
	}

	public Path getSaves() {
		return saves;
	}
}
