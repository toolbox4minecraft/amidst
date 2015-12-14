package amidst.clazz.symbolic.declaration;

import java.util.Collections;
import java.util.List;

import amidst.clazz.symbolic.SymbolicClassGraphCreationException;
import amidst.documentation.Immutable;
import amidst.logging.Log;

@Immutable
public class SymbolicClassDeclaration {
	private final String symbolicClassName;
	private final List<SymbolicConstructorDeclaration> constructors;
	private final List<SymbolicMethodDeclaration> methods;
	private final List<SymbolicFieldDeclaration> fields;

	public SymbolicClassDeclaration(String symbolicClassName,
			List<SymbolicConstructorDeclaration> constructors,
			List<SymbolicMethodDeclaration> methods,
			List<SymbolicFieldDeclaration> fields) {
		this.symbolicClassName = symbolicClassName;
		this.constructors = Collections.unmodifiableList(constructors);
		this.methods = Collections.unmodifiableList(methods);
		this.fields = Collections.unmodifiableList(fields);
	}

	public String getSymbolicClassName() {
		return symbolicClassName;
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

	public void handleMultipleMatches(String firstRealClassName,
			String otherRealClassName) {
		Log.w("Found class " + symbolicClassName + " again: "
				+ firstRealClassName + ", " + otherRealClassName);
	}

	public void handleMatch(String realClassName) {
		Log.i("Found class " + symbolicClassName + ": " + realClassName);
	}

	public void handleNoMatch() {
		Log.i("Missing class " + symbolicClassName);
	}

	public void handleMissing(ClassNotFoundException e,
			String symbolicClassName, String realClassName)
			throws SymbolicClassGraphCreationException {
		throw new SymbolicClassGraphCreationException(
				"unable to find the real class " + realClassName + " -> ("
						+ symbolicClassName + ")", e);
	}
}
