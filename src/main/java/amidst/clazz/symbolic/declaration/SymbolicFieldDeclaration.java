package amidst.clazz.symbolic.declaration;

import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;

@Immutable
public class SymbolicFieldDeclaration {
	private final String symbolicName;
	private final String realName;
	private final boolean isOptional;

	public SymbolicFieldDeclaration(String symbolicName, String realName, boolean isOptional) {
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.isOptional = isOptional;
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

	public void handleMissing(Exception e, String symbolicClassName, String realClassName)
			throws SymbolicClassGraphCreationException {
		String message = "unable to find the real class field " + realClassName + "." + realName + " -> "
				+ symbolicClassName + "." + symbolicName;
		if (isOptional) {
			AmidstLogger.info(message);
		} else {
			throw new SymbolicClassGraphCreationException(message, e);
		}
	}
}
