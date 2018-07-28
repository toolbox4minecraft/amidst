package amidst.mojangapi.minecraftinterface;

import amidst.documentation.Immutable;

@SuppressWarnings("serial")
@Immutable
public class MinecraftInterfaceCreationException extends Exception {
	public MinecraftInterfaceCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MinecraftInterfaceCreationException(String message) {
		super(message);
	}
}
