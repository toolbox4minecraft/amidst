package amidst.clazz.symbolic.declaration;

import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;

@Immutable
public class SymbolicMethodDeclaration {
	private final String symbolicName;
	private final String realName;
	private final boolean isOptional;
	private final SymbolicParameterDeclarationList parameters;

	public SymbolicMethodDeclaration(
			String symbolicName,
			String realName,
			boolean isOptional,
			SymbolicParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.isOptional = isOptional;
		this.parameters = parameters;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getRealName() {
		return realName;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public SymbolicParameterDeclarationList getParameters() {
		return parameters;
	}

	public void handleMissing(Exception e, String symbolicClassName, String realClassName)
			throws SymbolicClassGraphCreationException {
		String message = "unable to find the real class method " + realClassName + "." + realName
				+ parameters.getParameterString() + " -> " + symbolicClassName + "." + symbolicName;
		if (isOptional) {
			AmidstLogger.info(message);
		} else {
			throw new SymbolicClassGraphCreationException(message, e);
		}
	}
}
