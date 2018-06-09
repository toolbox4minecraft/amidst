package amidst.clazz.symbolic.declaration;

import java.util.Collections;
import java.util.List;

import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;

@Immutable
public class SymbolicClassDeclaration {
	private final String symbolicClassName;
	private final boolean isOptional;
	private final List<SymbolicConstructorDeclaration> constructors;
	private final List<SymbolicMethodDeclaration> methods;
	private final List<SymbolicFieldDeclaration> fields;

	public SymbolicClassDeclaration(
			String symbolicClassName,
			boolean isOptional,
			List<SymbolicConstructorDeclaration> constructors,
			List<SymbolicMethodDeclaration> methods,
			List<SymbolicFieldDeclaration> fields) {
		this.symbolicClassName = symbolicClassName;
		this.isOptional = isOptional;
		this.constructors = Collections.unmodifiableList(constructors);
		this.methods = Collections.unmodifiableList(methods);
		this.fields = Collections.unmodifiableList(fields);
	}

	public String getSymbolicClassName() {
		return symbolicClassName;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public List<SymbolicConstructorDeclaration> getConstructors() {
		return constructors;
	}

	public List<SymbolicMethodDeclaration> getMethods() {
		return methods;
	}

	public List<SymbolicFieldDeclaration> getFields() {
		return fields;
	}

	public void handleMultipleMatches(String firstRealClassName, String otherRealClassName) {
		AmidstLogger
				.warn("Found class {} again: {}, {}", symbolicClassName, firstRealClassName, otherRealClassName);
	}

	public void handleMatch(String realClassName) {
		AmidstLogger.info("Found class {}: {}", symbolicClassName, realClassName);
	}

	public void handleNoMatch() throws ClassNotFoundException {
		if (isOptional) {
			AmidstLogger.info("Missing class {}", symbolicClassName);
		} else {
			throw new ClassNotFoundException(
					"cannot find a real class matching the symbolic class " + symbolicClassName);
		}
	}

	public void handleMissing(ClassNotFoundException e, String realClassName)
			throws SymbolicClassGraphCreationException {
		String message = "unable to find the real class {}" + realClassName + " -> " + symbolicClassName;
		if (isOptional) {
			AmidstLogger.info(message);
		} else {
			throw new SymbolicClassGraphCreationException(message, e);
		}
	}
}
