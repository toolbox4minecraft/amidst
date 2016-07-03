package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@SuppressWarnings("serial")
@Immutable
public class LocalMinecraftInterfaceCreationException extends Exception {
	public LocalMinecraftInterfaceCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocalMinecraftInterfaceCreationException(String message) {
		super(message);
	}
}
