package amidst.clazz.symbolic;

import amidst.documentation.Immutable;

@Immutable
@SuppressWarnings("serial")
public class SymbolicClassGraphCreationException extends Exception {
	public SymbolicClassGraphCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
