package amidst.mojangapi.minecraftinterface;

@SuppressWarnings("serial")
public class MinecraftInterfaceException extends Exception {
	public MinecraftInterfaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public MinecraftInterfaceException(String message) {
		super(message);
	}
}
