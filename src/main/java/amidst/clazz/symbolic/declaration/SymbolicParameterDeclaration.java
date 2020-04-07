package amidst.clazz.symbolic.declaration;

import amidst.documentation.Immutable;

@Immutable
public class SymbolicParameterDeclaration {
	private final String type;
	private final boolean isSymbolic;
	private final int arrayDimensions;

	public SymbolicParameterDeclaration(String type, boolean isSymbolic) {
		this.type = type;
		this.isSymbolic = isSymbolic;
		this.arrayDimensions = 0;
	}
	
	public SymbolicParameterDeclaration(String type, boolean isSymbolic, int arrayDimensions) {
		this.type = type;
		this.isSymbolic = isSymbolic;
		this.arrayDimensions = arrayDimensions;
	}


	public String getType() {
		return type;
	}

	public boolean isSymbolic() {
		return isSymbolic;
	}
	
	public int getArrayDimensions() {
		return arrayDimensions;
	}

	public String getParameterString() {
		if (isSymbolic) {
			return "@" + type;
		} else {
			return type;
		}
	}
}
