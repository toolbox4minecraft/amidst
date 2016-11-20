package amidst.clazz.symbolic.declaration;

import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;

@Immutable
public class SymbolicConstructorDeclaration {
	private final String symbolicName;
	private final boolean isOptional;
	private final SymbolicParameterDeclarationList parameters;

	public SymbolicConstructorDeclaration(
			String symbolicName,
			boolean isOptional,
			SymbolicParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.isOptional = isOptional;
		this.parameters = parameters;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public SymbolicParameterDeclarationList getParameters() {
		return parameters;
	}

	public void handleMissing(Exception e, String symbolicClassName, String realClassName)
			throws SymbolicClassGraphCreationException {
		String message = "unable to find the real class constructor " + realClassName + ".<init>"
				+ parameters.getParameterString() + " -> " + symbolicClassName + "." + symbolicName;
		if (isOptional) {
			AmidstLogger.info(message);
		} else {
			throw new SymbolicClassGraphCreationException(message, e);
		}
	}
}
