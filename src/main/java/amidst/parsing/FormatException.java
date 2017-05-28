package amidst.parsing;

@SuppressWarnings("serial")
public class FormatException extends Exception {
	public FormatException(String message) {
		super(message);
	}

	public FormatException(Throwable cause) {
		super(cause);
	}

	public FormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
