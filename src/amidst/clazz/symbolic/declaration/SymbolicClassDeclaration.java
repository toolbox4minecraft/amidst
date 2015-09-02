package amidst.clazz.symbolic.declaration;

import java.util.Collections;
import java.util.List;

public class SymbolicClassDeclaration {
	private String symbolicClassName;
	private List<SymbolicConstructorDeclaration> constructors;
	private List<SymbolicMethodDeclaration> methods;
	private List<SymbolicPropertyDeclaration> properties;

	public SymbolicClassDeclaration(String symbolicClassName,
			List<SymbolicConstructorDeclaration> constructors,
			List<SymbolicMethodDeclaration> methods,
			List<SymbolicPropertyDeclaration> properties) {
		this.symbolicClassName = symbolicClassName;
		this.constructors = Collections.unmodifiableList(constructors);
		this.methods = Collections.unmodifiableList(methods);
		this.properties = Collections.unmodifiableList(properties);
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

	public List<SymbolicPropertyDeclaration> getProperties() {
		return properties;
	}
}
