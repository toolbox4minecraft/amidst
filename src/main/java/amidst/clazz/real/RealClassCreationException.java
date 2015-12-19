package amidst.clazz.real;

import amidst.documentation.Immutable;

@SuppressWarnings("serial")
@Immutable
public class RealClassCreationException extends Exception {
	public RealClassCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
