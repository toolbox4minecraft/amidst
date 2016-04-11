package amidst.mojangapi.file;

import java.io.FileNotFoundException;

@SuppressWarnings("serial")
public class DotMinecraftDirectoryNotFoundException extends FileNotFoundException {
	public DotMinecraftDirectoryNotFoundException(String message) {
		super(message);
	}
}
