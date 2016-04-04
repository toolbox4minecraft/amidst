package amidst.clazz.symbolic.declaration;

import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.documentation.Immutable;
import amidst.logging.Log;

@Immutable
public class SymbolicFieldDeclaration {
	private final String symbolicName;
	private final String declaration;
	private final DeclarationType declarationType;
	private final boolean isOptional;

	public enum DeclarationType {
		FIELDTYPE_BY_SYMBOLIC_CLASSNAME,
		FIELDTYPE_BY_REAL_TYPE,
		FIELDNAME_REAL_NAME
	}
	
	public SymbolicFieldDeclaration(String symbolicName, String declaration, 
			DeclarationType declarationType, boolean isOptional) {
		this.symbolicName = symbolicName;
		this.declaration = declaration;
		this.declarationType = declarationType;
		this.isOptional = isOptional;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getDeclaration() {
		return declaration;
	}

	public DeclarationType getDeclarationType() {
		return declarationType;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void handleMissing(Exception e, String symbolicClassName,
			String realClassName) throws SymbolicClassGraphCreationException {
		String message = "unable to find the real class field " + realClassName
				+ "." + (declarationType == DeclarationType.FIELDNAME_REAL_NAME ? declaration : "((" + declaration + ")*)") + 
				" -> " + symbolicClassName + "." + symbolicName;
		if (isOptional) {
			Log.i(message);
		} else {
			throw new SymbolicClassGraphCreationException(message, e);
		}
	}
}
