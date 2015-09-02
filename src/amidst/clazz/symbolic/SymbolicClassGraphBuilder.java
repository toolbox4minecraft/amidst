package amidst.clazz.symbolic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.real.RealClass;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicPropertyDeclaration;

public class SymbolicClassGraphBuilder {
	private ClassLoader classLoader;
	private Map<SymbolicClassDeclaration, RealClass> realClassesBySymbolicClassDeclaration;
	private Map<String, RealClass> realClassesBySymbolicClassName = new HashMap<String, RealClass>();
	private Map<String, SymbolicClass> symbolicClassesByRealClassName = new HashMap<String, SymbolicClass>();
	private Map<SymbolicClassDeclaration, SymbolicClassBuilder> symbolicClassBuildersBySymbolicClassDeclaration = new HashMap<SymbolicClassDeclaration, SymbolicClassBuilder>();

	public SymbolicClassGraphBuilder(
			ClassLoader classLoader,
			Map<SymbolicClassDeclaration, RealClass> realClassesBySymbolicClassDeclaration) {
		this.classLoader = classLoader;
		this.realClassesBySymbolicClassDeclaration = realClassesBySymbolicClassDeclaration;
	}

	public Map<String, SymbolicClass> create() {
		createSymbolicClasses();
		addConstructorsMethodsAndProperties();
		return createProduct();
	}

	private void createSymbolicClasses() {
		for (Entry<SymbolicClassDeclaration, RealClass> entry : realClassesBySymbolicClassDeclaration
				.entrySet()) {
			SymbolicClassDeclaration declaration = entry.getKey();
			RealClass realClass = entry.getValue();
			String symbolicClassName = declaration.getSymbolicClassName();
			String realClassName = realClass.getRealClassName();
			SymbolicClassBuilder builder = new SymbolicClassBuilder(
					classLoader, realClassesBySymbolicClassName,
					symbolicClassesByRealClassName,
					declaration.getSymbolicClassName(), realClassName);
			SymbolicClass symbolicClass = builder.create();
			realClassesBySymbolicClassName.put(symbolicClassName, realClass);
			symbolicClassesByRealClassName.put(realClassName, symbolicClass);
			symbolicClassBuildersBySymbolicClassDeclaration.put(declaration,
					builder);
		}
	}

	private void addConstructorsMethodsAndProperties() {
		for (Entry<SymbolicClassDeclaration, SymbolicClassBuilder> entry : symbolicClassBuildersBySymbolicClassDeclaration
				.entrySet()) {
			SymbolicClassDeclaration declaration = entry.getKey();
			SymbolicClassBuilder builder = entry.getValue();
			addConstructors(builder, declaration.getConstructors());
			addMethods(builder, declaration.getMethods());
			addProperties(builder, declaration.getProperties());
		}
	}

	private void addConstructors(SymbolicClassBuilder builder,
			List<SymbolicConstructorDeclaration> constructors) {
		for (SymbolicConstructorDeclaration constructor : constructors) {
			builder.addConstructor(constructor);
		}
	}

	private void addMethods(SymbolicClassBuilder builder,
			List<SymbolicMethodDeclaration> methods) {
		for (SymbolicMethodDeclaration method : methods) {
			builder.addMethod(method);
		}
	}

	private void addProperties(SymbolicClassBuilder builder,
			List<SymbolicPropertyDeclaration> properties) {
		for (SymbolicPropertyDeclaration property : properties) {
			builder.addProperty(property);
		}
	}

	private Map<String, SymbolicClass> createProduct() {
		Map<String, SymbolicClass> result = new HashMap<String, SymbolicClass>();
		for (Entry<SymbolicClassDeclaration, SymbolicClassBuilder> entry : symbolicClassBuildersBySymbolicClassDeclaration
				.entrySet()) {
			result.put(entry.getKey().getSymbolicClassName(), entry.getValue()
					.create());
		}
		return result;
	}
}
