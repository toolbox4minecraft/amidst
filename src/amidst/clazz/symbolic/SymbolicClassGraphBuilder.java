package amidst.clazz.symbolic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.real.ByteClass;
import amidst.clazz.real.ConstructorDeclaration;
import amidst.clazz.real.MethodDeclaration;
import amidst.clazz.real.PropertyDeclaration;

public class SymbolicClassGraphBuilder {
	private ClassLoader classLoader;
	private Map<String, ByteClass> realClassesBySymbolicClassName;
	private Map<String, SymbolicClass> symbolicClassesByRealClassName = new HashMap<String, SymbolicClass>();
	private Map<String, SymbolicClassBuilder> symbolicClassBuildersBySymbolicClassName = new HashMap<String, SymbolicClassBuilder>();

	public SymbolicClassGraphBuilder(ClassLoader classLoader,
			Map<String, ByteClass> realClassesBySymbolicClassName) {
		this.classLoader = classLoader;
		this.realClassesBySymbolicClassName = realClassesBySymbolicClassName;
	}

	public Map<String, SymbolicClass> create() {
		populateSymbolicClassMaps();
		addConstructorsMethodsAndProperties();
		return createProduct();
	}

	private void populateSymbolicClassMaps() {
		for (Entry<String, ByteClass> entry : realClassesBySymbolicClassName
				.entrySet()) {
			String symbolicClassName = entry.getKey();
			String realClassName = entry.getValue().getRealClassName();
			SymbolicClassBuilder builder = new SymbolicClassBuilder(
					classLoader, symbolicClassesByRealClassName,
					symbolicClassName, realClassName);
			symbolicClassesByRealClassName.put(realClassName, builder.create());
			symbolicClassBuildersBySymbolicClassName.put(symbolicClassName,
					builder);
		}
	}

	private void addConstructorsMethodsAndProperties() {
		for (Entry<String, ByteClass> entry : realClassesBySymbolicClassName
				.entrySet()) {
			ByteClass realClass = entry.getValue();
			SymbolicClassBuilder builder = symbolicClassBuildersBySymbolicClassName
					.get(entry.getKey());
			addConstructors(builder, realClass.getConstructors());
			addMethods(builder, realClass.getMethods());
			addProperties(builder, realClass.getProperties());
		}
	}

	private void addConstructors(SymbolicClassBuilder builder,
			List<ConstructorDeclaration> constructors) {
		for (ConstructorDeclaration constructor : constructors) {
			builder.addConstructor(realClassesBySymbolicClassName, constructor);
		}
	}

	private void addMethods(SymbolicClassBuilder builder,
			List<MethodDeclaration> methods) {
		for (MethodDeclaration method : methods) {
			builder.addMethod(realClassesBySymbolicClassName, method);
		}
	}

	private void addProperties(SymbolicClassBuilder builder,
			List<PropertyDeclaration> properties) {
		for (PropertyDeclaration property : properties) {
			builder.addProperty(property);
		}
	}

	private Map<String, SymbolicClass> createProduct() {
		Map<String, SymbolicClass> result = new HashMap<String, SymbolicClass>();
		for (Entry<String, SymbolicClassBuilder> entry : symbolicClassBuildersBySymbolicClassName
				.entrySet()) {
			result.put(entry.getKey(), entry.getValue().create());
		}
		return result;
	}
}
