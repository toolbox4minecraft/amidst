package amidst.clazz.symbolic.declaration;

import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.documentation.Immutable;

@Immutable
public class SymbolicConstructorDeclaration {
	private final String symbolicName;
	private final SymbolicParameterDeclarationList parameters;

	public SymbolicConstructorDeclaration(String symbolicName,
			SymbolicParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.parameters = parameters;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public SymbolicParameterDeclarationList getParameters() {
		return parameters;
	}

	public void handleMissing(Exception e, String symbolicClassName,
			String realClassName) throws SymbolicClassGraphCreationException {
		throw new SymbolicClassGraphCreationException(
				"unable to find the real class constructor " + realClassName
						+ ".<init>" + parameters.toString() + " -> ("
						+ symbolicClassName + "." + symbolicName + ")", e);
	}
}
