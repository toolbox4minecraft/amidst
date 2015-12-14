package amidst.clazz.symbolic.declaration;

import amidst.documentation.Immutable;

@Immutable
public class SymbolicParameterDeclaration {
	private final String type;
	private final boolean isSymbolic;

	public SymbolicParameterDeclaration(String type, boolean isSymbolic) {
		this.type = type;
		this.isSymbolic = isSymbolic;
	}

	public String getType() {
		return type;
	}

	public boolean isSymbolic() {
		return isSymbolic;
	}

	public String getParameterString() {
		if (isSymbolic) {
			return "@" + type;
		} else {
			return type;
		}
	}
}
