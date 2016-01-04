package amidst.mojangapi.file;

@SuppressWarnings("serial")
public class MojangApiParsingException extends Exception {
	public MojangApiParsingException(String message) {
		super(message);
	}

	public MojangApiParsingException(Throwable cause) {
		super(cause);
	}

	public MojangApiParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
