package amidst.clazz.real;

import amidst.documentation.Immutable;

@SuppressWarnings("serial")
@Immutable
public class JarFileParsingException extends Exception {
	public JarFileParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
